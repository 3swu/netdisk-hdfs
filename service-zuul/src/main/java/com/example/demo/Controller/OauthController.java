package com.example.demo.Controller;

import com.example.demo.Entity.User;
import com.example.demo.Service.OauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OauthController {

    @Autowired
    OauthService oauthService;

    @PostMapping("/user")
    public String register(@RequestBody User user) {
        int result = oauthService.userRegister(user.getUsername(), user.getPassword());
        if (result == -1) return "username existed";
        if (result == 0) return "failed";
        else return "success";
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) {
        User resultUser = oauthService.login(user.getUsername(), user.getPassword());
        if (resultUser == null) return null;
        else {
            resultUser.setPassword("");
            return resultUser;
        }
    }
}
