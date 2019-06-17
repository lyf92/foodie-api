package com.lyf.foodie.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {
    @Id
    private String id;
    private String name;
    private String url;
    private Date createTime;
    private Date updateTime;
}
