package com.lyf.foodie.service;

import com.lyf.foodie.exceptions.BaseException;
import com.lyf.foodie.exceptions.ResourceNotFoundException;
import com.lyf.foodie.model.Video;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static com.lyf.foodie.exceptions.ErrorCode.DELETE_VIDEO_FAILED;
import static com.lyf.foodie.exceptions.ErrorCode.UPLOAD_VICEO_TO_SERVER_FAILED;
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
    private MongoTemplate mongoTemplate;

    public void delete(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));

        List<Video> videos = mongoTemplate.find(query, Video.class);
        if(videos.isEmpty()) {
            log.error("视频不存在！");
            throw new ResourceNotFoundException(VIDEO_IS_NOT_FOUND, MessageFormat.format("删除视频失败！id为{0}的视频不存在。", fileId));
        }

        mongoTemplate.remove(query, Video.class);
        File file = new File(videoBasePath + videos.get(0).getName());
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

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件至服务器失败！", e);
            throw new BaseException(UPLOAD_VICEO_TO_SERVER_FAILED, "上传视频至服务器失败！");
        }
        String url = baseUrl + "/videos/" + fileName;
        Video newVideo = Video.builder()
                .name(fileName)
                .url(url)
                .description(description)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        mongoTemplate.save(newVideo);

        return newVideo;
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

    public List<Video> getVideos() {
        return mongoTemplate.findAll(Video.class);
    }

    public Video getVideo(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        return mongoTemplate.find(query, Video.class).get(0);
    }
}
