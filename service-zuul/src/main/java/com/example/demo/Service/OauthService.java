package com.example.demo.Service;

import com.example.demo.DAO.UserMapper;
import com.example.demo.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class OauthService {

    @Autowired
    UserMapper userMapper;

    public int userRegister(String username, String password) {
        User queryUser = userMapper.queryUserByName(username);
        if (queryUser != null) return -1; //username exists
        String dateFomt = "yyyy-MM-dd hh:mm:ss";
        if (userMapper.addUser(username, password, new SimpleDateFormat(dateFomt).format(new Date())) == 1)
            return 1; //registe success
        return 0; //failed
    }

    public User login(String username, String password) {
        User user = userMapper.queryUserByName(username);
        if (user == null) return null; //user not exists
        if (!password.equals(user.getPassword())) return null; //password error
        return user;
    }
}
