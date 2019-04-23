package com.lyf.foodie.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

    @Test
    public void should_return_upload_file_name() throws Exception {
        File file = new File("src/test/resources/test.mp4");
        MockMultipartFile multipartFile = new MockMultipartFile("video", new FileInputStream(file));

        mvc.perform(multipart("/api/files")
                    .file(multipartFile)
                    .accept(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}
