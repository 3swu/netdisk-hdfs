package com.example.demo.DAO;

import com.example.demo.Entity.SlicedFile;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TempFileUpLoad {

    @Update("update file set uploadtime=#{uploadTime}, currentblock=#{currentBlock}, isfinished=#{finished} where uuid=#{uuid}")
    void update(SlicedFile slicedFile);

    @Insert("insert into file(uuid, filename, uploadtime, filesize, fileblocknums, currentblock, isfinished, hdfspath, md5)" +
            " values (#{uuid}, #{fileName}, #{uploadTime}, #{fileSize}, #{fileBlockNums}, #{currentBlock}, #{finished}, #{hdfsPath}, #{md5})")
    int insert(SlicedFile slicedFile);

    @Select("select currentblock from file where uuid=#{uuid}")
    Integer queryCurrentBlock(String uuid);

    @Select("select filename from file where uuid=#{uuid}")
    String queryFileName(String uuid);

    @Select("select hdfspath from file where uuid=#{uuid}")
    String queryHdfsPath(String uuid);

    @Update("update file set hdfspath=#{hdfsPath} where uuid=#{uuid}")
    void updateHdfsPath(String hdfsPath, String uuid);

    @Delete("delete from file where uuid=#{uuid}")
    int deleteFile(String uuid);

}
