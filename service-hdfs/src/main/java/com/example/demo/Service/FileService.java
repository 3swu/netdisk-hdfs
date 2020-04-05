package com.example.demo.Service;

import com.example.demo.DAO.TempFileUpLoad;
import com.example.demo.Entity.SlicedFile;
import com.example.demo.Entity.WebServiceRequestFile;
import com.example.demo.FeignMapper.WebServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    TempFileUpLoad tempFileUpLoad;

    @Autowired
    HdfsService hdfsService;

    @Autowired
    WebServiceMapper webServiceMapper;

    private static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    private String tempFilePath;

    public FileService(@Value("${temp-file-storge-path}") String tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

    /*
    return : UUID of file
     */
    public String firstUploadFileBlock(MultipartFile fileBlock, SlicedFile slicedFile, Integer userIdFromHeader) {
        slicedFile.setUuid(UUID.randomUUID().toString());
        slicedFile.setUploadTime(new SimpleDateFormat(dateFormat).format(new Date()));
        slicedFile.setCurrentBlock(1);
        slicedFile.setFinished(slicedFile.getFileBlockNums() == 1);
        slicedFile.setHdfsPath("empty");

        String tempBlockStorgePath = tempFilePath + slicedFile.getUuid() + '_' + slicedFile.getCurrentBlock();
        try {
            fileBlock.transferTo(new File(tempBlockStorgePath));
        } catch (IOException e) {
            return "failed";
        }
        tempFileUpLoad.insert(slicedFile);
        if (slicedFile.getCurrentBlock().equals(slicedFile.getFileBlockNums())) {
            String mergedFilePath = mergeTempFiles(slicedFile.getUuid(), slicedFile.getFileBlockNums());
            hdfsService.uploadFileToHdfs(mergedFilePath, "/" + slicedFile.getUuid());
            tempFileUpLoad.updateHdfsPath("/" + slicedFile.getUuid(), slicedFile.getUuid());
            if (!(hdfsService.checkExists("/" + slicedFile.getUuid()) && uploadFileToWebService(slicedFile, userIdFromHeader)))
                return "failed";
        }
        return slicedFile.getUuid();
    }

    public boolean continueUploadFileBlock(MultipartFile fileBlock, SlicedFile slicedFile, Integer userIdFromHeader) {
        slicedFile.setUploadTime(new SimpleDateFormat(dateFormat).format(new Date()));
        if (!tempFileUpLoad.queryCurrentBlock(slicedFile.getUuid()).equals(slicedFile.getCurrentBlock() - 1))
            return false;
        try {
            String tempBlockStorgePath = tempFilePath + slicedFile.getUuid() + '_' + slicedFile.getCurrentBlock();
            fileBlock.transferTo(new File(tempBlockStorgePath));
        } catch (IOException e) {
            return false;
        }
        tempFileUpLoad.update(slicedFile);
        if (slicedFile.getCurrentBlock().equals(slicedFile.getFileBlockNums())) {
            String mergedFilePath = mergeTempFiles(slicedFile.getUuid(), slicedFile.getFileBlockNums());
            hdfsService.uploadFileToHdfs(mergedFilePath, "/" + slicedFile.getUuid());
            tempFileUpLoad.updateHdfsPath("/" + slicedFile.getUuid(), slicedFile.getUuid());
            return hdfsService.checkExists("/" + slicedFile.getUuid()) && uploadFileToWebService(slicedFile, userIdFromHeader);
        }
        return true;
    }

    private boolean uploadFileToWebService(SlicedFile slicedFile, Integer userIdFromHeader) {
        WebServiceRequestFile file = new WebServiceRequestFile();
        file.setFilename(slicedFile.getFileName());
        file.setFileSize(slicedFile.getFileSize());
        file.setFileTime(slicedFile.getUploadTime());
        file.setFileUuid(slicedFile.getUuid());
        file.setUserId(userIdFromHeader);
        return webServiceMapper.uploadFileToWebService(file).equals("true");
    }

    private String mergeTempFiles(String uuid, int blockNums) {
        String mergedFilePath = tempFilePath + uuid;
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new RandomAccessFile(new File(mergedFilePath), "rw").getChannel();
            RandomAccessFile randomAccessInputFile = null;
            for (int i = 1; i <= blockNums; i++) {
                String tempPath = tempFilePath + uuid + '_' + i;
                File tempFile = new File(tempPath);
                randomAccessInputFile = new RandomAccessFile(tempFile, "r");
                inputChannel = randomAccessInputFile.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
                while (inputChannel.read(byteBuffer) != -1) {
                    byteBuffer.flip();
                    outputChannel.write(byteBuffer);
                    byteBuffer.clear();
                }
                if (!tempFile.delete()) System.out.println("delete file error");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try {
                outputChannel.close();
                inputChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mergedFilePath;
    }

    public String downloadFile(HttpServletResponse response, String uuid) {
        String fileName = tempFileUpLoad.queryFileName(uuid);
        if (fileName == null) return "cannot find file";
        String hdfsPath = tempFileUpLoad.queryHdfsPath(uuid);
        byte[] fileBytes = hdfsService.openWithBytes(hdfsPath);
        try {
            ServletOutputStream servletOutputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            servletOutputStream.write(fileBytes);
        } catch (IOException e) {
            return "download error";
        }
        return "download success";
    }

    public boolean deleteFile(String uuid) {
        String hdfsPath = tempFileUpLoad.queryHdfsPath(uuid);
        if (hdfsPath == null) return false;
        if (tempFileUpLoad.deleteFile(uuid) != 1) return false;
        return hdfsService.delete(hdfsPath);
    }
}
