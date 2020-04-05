package com.example.demo.Entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserInfo implements Serializable {
    private Integer userId;
    private List<File> userFileList;
    private Long totalFilesSize;
    private Integer fileNums;
}