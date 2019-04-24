package com.lyf.foodie.controller;

import com.lyf.foodie.model.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/files")
    public String upload(@RequestParam("video") MultipartFile file) {
        Video video = Video.builder()
                .video(file.getName())
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        return mongoTemplate.save(video).getId();
    }
}
