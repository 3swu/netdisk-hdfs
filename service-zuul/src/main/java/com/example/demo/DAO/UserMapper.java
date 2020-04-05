package com.example.demo.DAO;

import com.example.demo.Entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into user (username, password, regtime) values (#{username}, #{password}, #{regtime})")
    int addUser(@Param("username") String username, @Param("password") String password, @Param("regtime") String regtime);

    @Select("select * from user where username=#{username}")
    User queryUserByName(@Param("username") String username);

}
