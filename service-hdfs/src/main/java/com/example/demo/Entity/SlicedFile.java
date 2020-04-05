package com.example.demo.Entity;

public class SlicedFile {
    private String uuid;
    private String fileName;
    private Integer currentBlock;
    private Integer fileBlockNums;
    private String md5;
    private String uploadTime;
    private Integer fileSize;
    private String hdfsPath;
    private boolean finished;

    public SlicedFile(String uuid, String fileName, Integer currentBlock, Integer fileBlockNums, String md5, String uploadTime, Integer fileSize, String hdfsPath, boolean isFinised) {
        this.uuid = uuid;
        this.fileName = fileName;
        this.currentBlock = currentBlock;
        this.fileBlockNums = fileBlockNums;
        this.md5 = md5;
        this.uploadTime = uploadTime;
        this.fileSize = fileSize;
        this.hdfsPath = hdfsPath;
        this.finished = isFinised;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getCurrentBlock() {
        return currentBlock;
    }

    public void setCurrentBlock(Integer currentBlock) {
        this.currentBlock = currentBlock;
    }

    public Integer getFileBlockNums() {
        return fileBlockNums;
    }

    public void setFileBlockNums(Integer fileBlockNums) {
        this.fileBlockNums = fileBlockNums;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public SlicedFile() {}
}
