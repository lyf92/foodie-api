package com.lyf.foodie.vo;

import com.lyf.foodie.entity.Video;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Date;

@Getter
@Builder
public class VideoVO {
    private String id;

    private String name;

    private String description;

    private String url;

    private String createUserId;

    private boolean liked;

    private Date createTime;

    private Date updateTime;

    public static VideoVO buildFrom(Video video, String userId) {
        return VideoVO.builder()
                .id(video.getId())
                .name(video.getName())
                .description(video.getDescription())
                .url(video.getUrl())
                .createUserId(video.getCreateUserId())
                .createTime(video.getCreateTime())
                .updateTime(video.getUpdateTime())
                .liked(video.getLikes().stream()
                        .anyMatch(like -> like.getId().equals(userId)))
                .build();
    }
}
