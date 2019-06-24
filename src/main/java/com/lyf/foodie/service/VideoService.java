package com.lyf.foodie.service;

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
import java.util.Date;
import java.util.List;

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
            // TODO: throw exception
            return;
        }

        mongoTemplate.remove(query, Video.class);
        File file = new File(videoBasePath + videos.get(0).getName());
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            log.error("删除视频失败！");
            // TODO: throw exception
        }
    }

    public Video uploadVideo(MultipartFile file, String description) {
        if (file.isEmpty()) {
            log.error("上传视频不可为空！");
            // TODO: throw exception
            return null;
        }

        String fileName = file.getOriginalFilename();
        String path = videoBasePath + fileName;
        log.debug("File path in server is {}.", path);

        File dest = new File(path);
        if(dest.exists()) {
            log.error("文件已存在！");
            // TODO: throw exception
            return null;
        }

        if(!dest.getParentFile().exists()) {
            log.debug("创建新目录！");
            dest.getParentFile().mkdirs();
        }

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("上传文件至服务器失败！", e);
            // TODO: throw exception
            return null;
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

    public List<Video> getVideos() {
        return mongoTemplate.findAll(Video.class);
    }

    public Video getVideo(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        return mongoTemplate.find(query, Video.class).get(0);
    }
}
