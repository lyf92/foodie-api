package com.lyf.foodie.controller;

import com.lyf.foodie.service.VideoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(VideoController.class)
public class VideoControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private VideoService videoService;

    @Test
    public void should_upload_file_successfully() throws Exception {
        File file = new File("src/test/resources/test.mp4");
        MockMultipartFile multipartFile = new MockMultipartFile("video", "video", MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream(file));

        mvc.perform(multipart("/api/files")
                .file(multipartFile)
                .param("description", "这是一个神奇的视频～"))
                .andExpect(status().isOk());
    }
}
