package com.gy.gmall.manager.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.util.StringUtil;

import java.io.IOException;

@RestController
public class FileUploadController {

    //获取配置文件中的信息
    @Value("${fileServer.url}")
    private  String fileUrl;

    @RequestMapping(value="/fileUpload",method = RequestMethod.POST)
    public String fileUpload(@RequestParam("file")MultipartFile file) throws IOException, MyException {
        String imgUrl = fileUrl;
        if(file!=null){
            //
            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageClient storageClient = new StorageClient(trackerServer,null);
            String fileName = file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(fileName,".");

            String[] uploadFile = storageClient.upload_file(file.getBytes(),extName,null);
            imgUrl =fileUrl;
            for (int i = 0; i < uploadFile.length; i++) {
                String path = uploadFile[i];
                imgUrl+="/"+path;

            }
        }
        System.out.println("路径 = " + imgUrl);
        return imgUrl;
    }
}
