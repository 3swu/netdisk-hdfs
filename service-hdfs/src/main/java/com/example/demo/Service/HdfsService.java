package com.example.demo.Service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HdfsService {

    private Logger logger = LoggerFactory.getLogger(HdfsService.class);

//    @Value("${hdfs.path}")
    private String defaultHdfsUri;

    private Configuration conf;

    public HdfsService(@Value("${hdfs.path}") String hdfsUri) {
        this.defaultHdfsUri = hdfsUri;
        this.conf = new Configuration();
        this.conf.set("fs.defaultFS", defaultHdfsUri);
    }

    private FileSystem getFileSystem() throws IOException {
        return FileSystem.get(this.conf);
    }

    public boolean checkExists(String path) {
        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();
            String hdfsPath = generateHdfsPath(path);
            return fileSystem.exists(new Path(hdfsPath));
        } catch (IOException e) {
            logger.error(MessageFormat.format("check exists error, path: {0}", path), e);
            return false;
        } finally {
            close(fileSystem);
        }
    }

    private String generateHdfsPath(String dstPath) {
        String hdfsPath = defaultHdfsUri;
        if (dstPath.startsWith("/"))
            hdfsPath += dstPath;
        else
            hdfsPath += "/" + dstPath;
        return hdfsPath;
    }

    public boolean mkdir(String path) {
        if (checkExists(path)) return true;
        else {
            FileSystem fileSystem = null;
            try {
                fileSystem = getFileSystem();
                String hdfsPath = generateHdfsPath(path);
                return fileSystem.mkdirs(new Path(hdfsPath));
            } catch (IOException e) {
                logger.error(MessageFormat.format("mkdir error, path: {0}", path), e);
                return false;
            } finally {
                close(fileSystem);
            }
        }
    }

    private void close(FileSystem fileSystem) {
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void uploadFileToHdfs(boolean delSrc, boolean overWrite, String srcFile, String dstPath) {
        Path localSrcPath = new Path(srcFile);
        Path hdfsDstPath = new Path(generateHdfsPath(dstPath));

        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();
            fileSystem.copyFromLocalFile(delSrc, overWrite, localSrcPath, hdfsDstPath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("upload file to hdfs error, srcPath: {0}, dstPath: {1}", srcFile, dstPath), e);
        } finally {
            close(fileSystem);
        }
    }

    public void uploadFileToHdfs(String srcFile, String dstPath) {
        uploadFileToHdfs(true, true, srcFile, dstPath);
    }

    public List<Map<String, Object>> listFiles(String path) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (checkExists(path)) {
            FileSystem fileSystem = null;
            try {
                fileSystem = getFileSystem();

                String hdfsPath = generateHdfsPath(path);
                FileStatus[] statuses = fileSystem.listStatus(new Path(hdfsPath));

                if (statuses != null) {
                    for (FileStatus fileStatus : statuses) {
                        Map<String, Object> fileMap = new HashMap<>(2);
                        fileMap.put("path", fileStatus.getPath().toString());
                        fileMap.put("idDir", fileStatus.isDirectory());
                        result.add(fileMap);
                    }
                }
            } catch (IOException e) {
                logger.error(MessageFormat.format("list files error, path: {0}", path), e);
                return null;
            } finally {
                close(fileSystem);
            }
        }
        return result;
    }

    public byte[] openWithBytes(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;
        FSDataInputStream inputStream = null;
        try {
            fileSystem = getFileSystem();
            inputStream = fileSystem.open(hdfsPath);
            return IOUtils.readFullyToByteArray(inputStream);
        } catch (IOException e) {
            logger.error(MessageFormat.format("open file error, path: {0}", path), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("close inputStream error");
                }
            }
            close(fileSystem);
        }
        return null;
    }

    public boolean delete(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;
        try {
            fileSystem = getFileSystem();
            return fileSystem.delete(hdfsPath, true);
        } catch (IOException e) {
            logger.error(MessageFormat.format("delete file error, path: {0}", path), e);
        } finally {
            close(fileSystem);
        }
        return false;
    }
}
