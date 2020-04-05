package com.example.demo.FeignMapper;

import com.example.demo.Entity.WebServiceRequestFile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-web")
public interface WebServiceMapper {
    @PostMapping(value = "/file/upload")
    String uploadFileToWebService(@RequestBody WebServiceRequestFile file);
}
