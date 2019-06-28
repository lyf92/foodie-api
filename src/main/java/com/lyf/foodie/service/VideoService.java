package com.lyf.foodie.service;

import com.lyf.foodie.entity.User;
import com.lyf.foodie.entity.Video;
import com.lyf.foodie.exceptions.BaseException;
import com.lyf.foodie.exceptions.ResourceNotFoundException;
import com.lyf.foodie.repository.UserRepository;
import com.lyf.foodie.repository.VideoRepository;
import com.lyf.foodie.vo.VideoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static com.lyf.foodie.exceptions.ErrorCode.DELETE_VIDEO_FAILED;
import static com.lyf.foodie.exceptions.ErrorCode.UPLOAD_VIDEO_TO_SERVER_FAILED;
import static com.lyf.foodie.exceptions.ErrorCode.USER_IS_NOT_FOUND;
import static com.lyf.foodie.exceptions.ErrorCode.VIDEO_IS_EXISTED;
import static com.lyf.foodie.exceptions.ErrorCode.VIDEO_IS_NOT_FOUND;

@Slf4j
@Service
public class VideoService {
    @Value("${video.base-path}")
    private String videoBasePath;

    @Value("${base-url}")
    private String baseUrl;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    public void delete(String fileId) {
        Video video = videoRepository.findById(fileId).orElseThrow(
                () -> new ResourceNotFoundException(VIDEO_IS_NOT_FOUND, MessageFormat.format("删除视频失败！id为{0}的视频不存在。", fileId)));

        videoRepository.deleteById(fileId);
        File file = new File(videoBasePath + video.getName());
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            log.error("删除视频失败！");
            throw new BaseException(DELETE_VIDEO_FAILED, MessageFormat.format("删除视频失败！id为{0}的视频删除失败。", fileId));
        }
    }

    public Video uploadVideo(MultipartFile file, String description) {
        String fileName = file.getOriginalFilename();
        String path = videoBasePath + fileName;
        log.debug("File path in server is {}.", path);

        File dest = new File(path);
        checkFileExistence(fileName, dest);
        createDirectory(dest);
        uploadToServer(file, dest);
        return saveToDB(description, fileName);
    }

    private Video saveToDB(String description, String fileName) {
        String url = baseUrl + "/videos/" + fileName;
        Video newVideo = Video.builder()
                .name(fileName)
                .url(url)
                .description(description)
                .build();
        videoRepository.save(newVideo);
        return newVideo;
    }

    private void uploadToServer(MultipartFile file, File dest) {
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件至服务器失败！", e);
            throw new BaseException(UPLOAD_VIDEO_TO_SERVER_FAILED, "上传视频至服务器失败！");
        }
    }

    private void createDirectory(File dest) {
        if(!dest.getParentFile().exists()) {
            log.debug("创建新目录！");
            dest.getParentFile().mkdirs();
        }
    }

    private void checkFileExistence(String fileName, File dest) {
        if(dest.exists()) {
            log.error("文件已存在！");
            throw new BaseException(VIDEO_IS_EXISTED, MessageFormat.format("名为{0}的视频已存在！", fileName));
        }
    }

    public List<VideoVO> getVideos(String userId) {
        List<VideoVO> videos = new ArrayList<>();
        videoRepository.findAll().forEach(video -> {
            videos.add(VideoVO.buildFrom(video, userId));
        });
        return videos;
    }

    public Video getVideo(String fileId) {
        return videoRepository.findById(fileId).orElseThrow(
                () -> new ResourceNotFoundException(VIDEO_IS_NOT_FOUND, MessageFormat.format("id为{0}的视频不存在。", fileId)));
    }

    public void favorite(String userId, String fileId) {
        Video video = videoRepository.findById(fileId).orElseThrow(
                () -> new ResourceNotFoundException(VIDEO_IS_NOT_FOUND, MessageFormat.format("id为{0}的视频不存在。", fileId)));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(USER_IS_NOT_FOUND, MessageFormat.format("id为{0}的用户不存在。", userId)));
        user.addVideo(video);
        userRepository.save(user);
    }

    public void unFavorite(String userId, String fileId) {
        Video video = videoRepository.findById(fileId).orElseThrow(
                () -> new ResourceNotFoundException(VIDEO_IS_NOT_FOUND, MessageFormat.format("id为{0}的视频不存在。", fileId)));
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(USER_IS_NOT_FOUND, MessageFormat.format("id为{0}的用户不存在。", userId)));
        user.removeVideo(video);
        userRepository.save(user);
    }
}
