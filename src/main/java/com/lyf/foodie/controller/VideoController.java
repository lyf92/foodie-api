package com.lyf.foodie.controller;

import com.lyf.foodie.entity.Video;
import com.lyf.foodie.service.VideoService;
import com.lyf.foodie.vo.VideoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @PostMapping("/files")
    public Video upload(@RequestParam("video") @Valid @NotNull MultipartFile file,
                        @RequestParam("description") @Valid @NotEmpty String description) {
        return videoService.uploadVideo(file, description);
    }

    @GetMapping("/files")
    public List<VideoVO> getFiles(@RequestParam(value = "userId", required = false) String userId) {
        return videoService.getVideos(userId);
    }

    @GetMapping("/files/{fileId}")
    public Video getById(@PathVariable(name = "fileId") String fileId) {
        return videoService.getVideo(fileId);
    }

    @DeleteMapping("/files/{fileId}")
    public void delete(@PathVariable(name = "fileId") String fileId) {
        videoService.delete(fileId);
    }

    @PostMapping("/users/{userId}/files/{fileId}")
    public void favorite(@PathVariable("userId") String userId,
                         @PathVariable("fileId") String fileId,
                         @RequestParam(value = "isFav", required = false) Boolean isFav) {
        if (isFav == null || isFav) {
            videoService.favorite(userId, fileId);
        } else {
            videoService.unFavorite(userId, fileId);
        }
    }
}
