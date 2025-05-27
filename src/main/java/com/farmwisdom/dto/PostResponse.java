package com.farmwisdom.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostResponse {
    private Long id;
    private Long userId;
    private String author;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String content;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isTop;
    private Boolean isEssence;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}