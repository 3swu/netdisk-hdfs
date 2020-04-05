package com.example.demo.Service;

import com.example.demo.Entity.File;
import com.example.demo.Entity.UserInfo;
import com.example.demo.FeignMapper.HdfsServiceMapper;
import com.example.demo.Mapper.FileMapper;
import com.example.demo.Util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class FileService {

    @Autowired
    FileMapper fileMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    HdfsServiceMapper hdfsServiceMapper;

    public UserInfo getUserInfo(Integer userId) {
        UserInfo userInfo;
        String redisKey = "userinfo_" + userId;
        if (redisUtil.hasKey(redisKey))
            userInfo = (UserInfo) redisUtil.getValue(redisKey);
        else {
            userInfo = updateUserInfoToRedis(userId);
        }
        return userInfo;
    }

    public boolean deleteFile(Integer id, Integer userId, String fileUuid) {
        if (fileMapper.deleteFileByFileId(id, userId) != 1)
            return false;

        // delete from redis
        String redisKey = "userinfo_" + userId;
        if (redisUtil.hasKey(redisKey)) {
            UserInfo userInfo = (UserInfo) redisUtil.getValue(redisKey);
            List<File> userFileList = userInfo.getUserFileList();
            for (File file : userFileList) {
                if (file.getId().equals(id)) {
                    userFileList.remove(file);
                    userInfo.setFileNums(userInfo.getFileNums() - 1);
                    userInfo.setTotalFilesSize(userInfo.getTotalFilesSize() - file.getFileSize());
                }
            }
            userInfo.setUserFileList(userFileList);
            redisUtil.addKey(redisKey, userInfo, 1, TimeUnit.DAYS);
        }

        // delete file from hdfs if no one user has this file
        if (fileMapper.queryFileExistsNums(fileUuid) == 0) {
            if (hdfsServiceMapper.deleteFileFromHdfs(fileUuid).equals("false"))
                return false;
        }
        return false;
    }

    public boolean addFile(File file) {
        if (fileMapper.addFile(file) == 1) {
            // update redis
            updateUserInfoToRedis(file.getUserId());
            return true;
        }
        return false;
    }

    private UserInfo updateUserInfoToRedis(Integer userId) {
        String redisKey = "userinfo_" + userId;
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setUserFileList(fileMapper.getAllFilesByUserId(userId));
        Long totalFileSize = 0L;
        for (File file : userInfo.getUserFileList())
            totalFileSize += file.getFileSize();
        userInfo.setTotalFilesSize(totalFileSize);
        userInfo.setFileNums(userInfo.getUserFileList().size());

        // put userInfo into redis
        redisUtil.addKey(redisKey, userInfo, 1, TimeUnit.DAYS);
        return userInfo;
    }
}
