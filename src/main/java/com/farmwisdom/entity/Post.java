package com.farmwisdom.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.farmwisdom.enums.PostType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("posts")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度必须在2-100字符之间")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(min = 10, message = "内容至少需要10个字符")
    private String content;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String type = PostType.ARTICLE.name();

    private Integer viewCount = 0;
    private Integer likeCount = 0;
    private Integer commentCount = 0;
    private Integer collectCount = 0;

    private Boolean isTop = false;
    private Boolean isEssence = false;
    private Boolean isLocked = false;

    private String status = "PUBLISHED";

    @TableField(exist = false)
    private List<String> images;

    @TableField(exist = false)
    private List<String> videos;

    @TableField(exist = false)
    private List<String> attachments;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    public boolean isQuestion() {
        return PostType.QUESTION.name().equals(this.type);
    }

    public boolean isLecture() {
        return PostType.LECTURE.name().equals(this.type);
    }
}