package com.lyf.foodie.service;

import com.lyf.foodie.exceptions.BaseException;
import com.lyf.foodie.exceptions.ResourceNotFoundException;
import com.lyf.foodie.model.Video;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileUtils.class})
@TestPropertySource(properties = {
        "video.base-path=/user/video/",
        "base-url=http://localhost:8080"
})
public class VideoServiceTest {

    @InjectMocks
    private VideoService videoService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    public void should_get_video_by_id_from_mongo_successfully() {
        List<Video> videos = Collections.singletonList(Video.builder()
                .id("video01")
                .name("这是一个神奇的视频")
                .description("这是一个神奇的视频啊")
                .build());
        Query query = Query.query(Criteria.where("_id").is("video01"));
        when(mongoTemplate.find(query, Video.class)).thenReturn(videos);

        Video video = videoService.getVideo("video01");

        assertThat(video.getId(), is("video01"));
        assertThat(video.getName(), is("这是一个神奇的视频"));
    }

    @Test
    public void should_get_videos_from_mongo_successfully() throws Exception {
        List<Video> videos = Arrays.asList(Video.builder().id("video01").name("这是一个神奇的视频").description("这是一个神奇的视频啊").build(),
                Video.builder().id("video02").name("这是一个神奇的视频2").description("这是一个神奇的视频啊2").build());
        when(mongoTemplate.findAll(Video.class)).thenReturn(videos);

        List<Video> videoList = videoService.getVideos();

        assertThat(videoList.size(), is(2));
        assertThat(videoList.get(0).getId(), is("video01"));
        assertThat(videoList.get(0).getName(), is("这是一个神奇的视频"));
        assertThat(videoList.get(1).getId(), is("video02"));
        assertThat(videoList.get(1).getName(), is("这是一个神奇的视频2"));
    }

    @Test
    public void should_delete_video_successfully() throws Exception {
        List<Video> videos = Collections.singletonList(Video.builder()
                .id("video01")
                .name("这是一个神奇的视频")
                .description("这是一个神奇的视频啊")
                .build());
        Query query = Query.query(Criteria.where("_id").is("video01"));
        when(mongoTemplate.find(query, Video.class)).thenReturn(videos);
        when(mongoTemplate.remove(query, Video.class)).thenReturn(null);
        mockStatic(FileUtils.class);
        doNothing().when(FileUtils.class);
        FileUtils.forceDelete(any());

        videoService.delete("video01");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void should_throw_error_when_video_is_not_exist() {
        Query query = Query.query(Criteria.where("_id").is("video01"));
        when(mongoTemplate.find(query, Video.class)).thenReturn(new ArrayList<>());

        videoService.delete("video01");
    }

    @Test(expected = BaseException.class)
    public void should_throw_error_when_delete_video_failed() throws Exception {
        List<Video> videos = Collections.singletonList(Video.builder()
                .id("video01")
                .name("这是一个神奇的视频")
                .description("这是一个神奇的视频啊")
                .build());
        Query query = Query.query(Criteria.where("_id").is("video01"));
        when(mongoTemplate.find(query, Video.class)).thenReturn(videos);
        when(mongoTemplate.remove(query, Video.class)).thenReturn(null);
        mockStatic(FileUtils.class);
        doThrow(new IOException()).when(FileUtils.class);
        FileUtils.forceDelete(any());

        videoService.delete("video01");
    }

}