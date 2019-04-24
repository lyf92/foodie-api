package com.lyf.foodie.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Document
@Builder
@Data
public class Video {
    @Id
    private String id;

    private String video;

    private Date createTime;

    private Date updateTime;
}
