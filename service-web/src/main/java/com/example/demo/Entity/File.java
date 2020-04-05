package com.example.demo.Entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class File implements Serializable {
    private Integer id;
    private Integer userId;
    private String fileUuid;
    private String filename;
    private String fileTime;
    private Integer fileSize;
}
