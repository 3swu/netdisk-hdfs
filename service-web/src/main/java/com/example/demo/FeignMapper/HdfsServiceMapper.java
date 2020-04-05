package com.example.demo.FeignMapper;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-hdfs")
public interface HdfsServiceMapper {
    @DeleteMapping(value = "/file/{uuid}")
    String deleteFileFromHdfs(@PathVariable("uuid") String uuid);

}
