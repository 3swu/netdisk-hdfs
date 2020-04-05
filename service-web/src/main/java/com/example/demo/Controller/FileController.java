package com.example.demo.Controller;

import com.example.demo.Entity.File;
import com.example.demo.Entity.UserInfo;
import com.example.demo.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileService fileService;

    @PostMapping("/upload")
    public boolean uploadFile(@RequestBody File file) {
        return fileService.addFile(file);
    }

    @GetMapping("/allfiles")
    public UserInfo getUserInfo(HttpServletRequest request) {
        Integer userid = Integer.valueOf(request.getHeader("id"));
        return fileService.getUserInfo(userid);
    }

    @DeleteMapping("/remove")
    public boolean deleteFile(@RequestParam("id") Integer id,
                              @RequestParam("uuid") String fileUuid,
                              HttpServletRequest request) {
        Integer userId = Integer.valueOf(request.getHeader("id"));
        return fileService.deleteFile(id, userId, fileUuid);
    }

}
