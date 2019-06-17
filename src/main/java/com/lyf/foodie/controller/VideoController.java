package com.lyf.foodie.controller;

import com.lyf.foodie.model.Video;
import com.lyf.foodie.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/files")
    public Video upload(@RequestParam("video") MultipartFile file) {
        return videoService.uploadVideo(file);
    }

    @GetMapping("/files")
    public List<Video> getFiles() {
        return videoService.getVideos();
    }

    @GetMapping("/files/{fileId}")
    public Video getById(@PathVariable(name = "fileId") String fileId) {
        return videoService.getVideo(fileId);
    }

    @DeleteMapping("/files/{fileId}")
    public void delete(@PathVariable(name = "fileId") String fileId) {
        videoService.delete(fileId);
    }
}
