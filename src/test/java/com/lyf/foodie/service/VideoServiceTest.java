package com.lyf.foodie.service;

import com.lyf.foodie.entity.Video;
import com.lyf.foodie.exceptions.BaseException;
import com.lyf.foodie.exceptions.ResourceNotFoundException;
import com.lyf.foodie.repository.VideoRepository;
import com.lyf.foodie.vo.VideoVO;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
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
    private VideoRepository videoRepository;

    @Test
    public void should_get_video_by_id_from_mongo_successfully() {
        Optional<Video> temp = Optional.of(Video.builder()
                .id("video01")
                .name("这是一个神奇的视频")
                .description("这是一个神奇的视频啊")
                .build());
        when(videoRepository.findById(any())).thenReturn(temp);

        Video video = videoService.getVideo("video01");

        assertThat(video.getId(), is("video01"));
        assertThat(video.getName(), is("这是一个神奇的视频"));
    }

    @Test
    public void should_get_videos_from_mongo_successfully() {
        List<Video> videos = Arrays.asList(Video.builder().id("video01").name("这是一个神奇的视频").description("这是一个神奇的视频啊").likes(Collections.emptyList()).build(),
                Video.builder().id("video02").name("这是一个神奇的视频2").description("这是一个神奇的视频啊2").likes(Collections.emptyList()).build());
        when(videoRepository.findAll()).thenReturn(videos);

        List<VideoVO> videoList = videoService.getVideos("0");

        assertThat(videoList.size(), is(2));
        assertThat(videoList.get(0).getId(), is("video01"));
        assertThat(videoList.get(0).getName(), is("这是一个神奇的视频"));
        assertThat(videoList.get(1).getId(), is("video02"));
        assertThat(videoList.get(1).getName(), is("这是一个神奇的视频2"));
    }

    @Test
    public void should_delete_video_successfully() throws Exception {
        Optional<Video> temp = Optional.of(Video.builder()
                .id("video01")
                .name("这是一个神奇的视频")
                .description("这是一个神奇的视频啊")
                .build());
        when(videoRepository.findById(any())).thenReturn(temp);
        doNothing().when(videoRepository).deleteById(any());
        mockStatic(FileUtils.class);
        doNothing().when(FileUtils.class);
        FileUtils.forceDelete(any());

        videoService.delete("video01");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void should_throw_error_when_video_is_not_exist() {
        when(videoRepository.findById(any())).thenReturn(Optional.empty());

        videoService.delete("video01");
    }

    @Test(expected = BaseException.class)
    public void should_throw_error_when_delete_video_failed() throws Exception {
        Optional<Video> temp = Optional.of(Video.builder()
                .id("video01")
                .name("这是一个神奇的视频")
                .description("这是一个神奇的视频啊")
                .build());
        when(videoRepository.findById(any())).thenReturn(temp);
        doNothing().when(videoRepository).deleteById(any());
        mockStatic(FileUtils.class);
        doThrow(new IOException()).when(FileUtils.class);
        FileUtils.forceDelete(any());

        videoService.delete("video01");
    }

    @Ignore
    @Test
    public void should_upload_video_successfully() throws Exception {
        videoService = spy(new VideoService());

        MockMultipartFile multipartFile = mock(MockMultipartFile.class);

        doNothing().when(videoService, "checkFileExistence", any(), any());
        doNothing().when(videoService, "createDirectory", any());
        doNothing().when(videoService, "uploadToServer", any(), any());
        doReturn(Video.builder().id("video01").build()).when(videoService, "saveToDB", any(), any());

        Video video = videoService.uploadVideo(multipartFile, "这是一个神奇的视频");

        assertThat(video.getId(), is("video01"));
    }

}