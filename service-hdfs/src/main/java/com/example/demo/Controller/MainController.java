package com.example.demo.Controller;

import com.example.demo.Entity.SlicedFile;
import com.example.demo.Service.FileService;
import com.example.demo.Service.HdfsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class MainController {

    @Autowired
    HdfsService hdfsService;

    @Autowired
    FileService fileService;

    @PostMapping(value = "/dir")
    public String mkdir(@RequestBody String path) {
        if (hdfsService.mkdir(path))
            return "mkdir success";
        return "mkdir failed";
    }

    @GetMapping("/list")
    public List<Map<String, Object>> listFiles(@RequestParam String dir) {
        return hdfsService.listFiles(dir);
    }

    @PostMapping("/{path}")
    public String storgeFile(@RequestBody String filePath, @PathVariable String path) {
        hdfsService.uploadFileToHdfs(filePath, path);
        return "success";
    }

    @PostMapping("/upload")
    public String upLoadFile(@RequestParam("fileBlock") MultipartFile fileBlock,
                             @RequestParam("uuid") String uuid,
                             @RequestParam("filename") String filename,
                             @RequestParam("filesize") Integer filesize,
                             @RequestParam("fileBlockNums") Integer fileBlockNums,
                             @RequestParam("currentBlock") Integer currentBlock,
                             @RequestParam("md5") String md5,
                             HttpServletRequest request) {
        // get user id from header
        Integer userId = Integer.valueOf(request.getHeader("id"));

        SlicedFile slicedFile = new SlicedFile();
        slicedFile.setFinished(currentBlock.equals(fileBlockNums));
        slicedFile.setFileName(filename);
        slicedFile.setFileSize(filesize);
        slicedFile.setFileBlockNums(fileBlockNums);
        slicedFile.setCurrentBlock(currentBlock);
        slicedFile.setMd5(md5);
        if (currentBlock != 1) {
            slicedFile.setUuid(uuid);
            if (fileService.continueUploadFileBlock(fileBlock, slicedFile, userId))
                return "success";
            return "failed";
        }
        return fileService.firstUploadFileBlock(fileBlock, slicedFile, userId);
    }

    @GetMapping("/{uuid}")
    public String fileDownload(HttpServletResponse response, @PathVariable String uuid) {
        return fileService.downloadFile(response, uuid);
    }

    @DeleteMapping("/{uuid}")
    public boolean fileDelete(@PathVariable String uuid) {
        return fileService.deleteFile(uuid);
    }
}
