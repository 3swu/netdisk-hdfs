package com.example.demo.Mapper;

import com.example.demo.Entity.File;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {

    @Insert("insert into file (userid, fileuuid, filename, filetime, filesize) values (#{userId}, #{fileUuid}, #{filename}, #{fileTime}, #{fileSize})")
    int addFile(File file);

    @Select("select id, fileuuid, filename, filetime, filesize from file where userid=#{userId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "fileUuid", column = "fileuuid"),
            @Result(property = "filename", column = "filename"),
            @Result(property = "fileTime", column = "filetime"),
            @Result(property = "fileSize", column = "filesize")
    })
    List<File> getAllFilesByUserId(Integer userId);

    @Delete("delete from file where id=#{id} and userid=#{userId}")
    int deleteFileByFileId(Integer id, Integer userId);

    @Select("select count(id) from file where fileuuid=#{fileUuid}")
    int queryFileExistsNums(String fileUuid);
}
