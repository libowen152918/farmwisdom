package com.farmwisdom.entity;

import com.baomidou.mybatisplus.annotation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comments")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;

    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private Long parentId;

    @NotBlank(message = "评论内容不能为空")
    private String content;

    private Integer likeCount = 0;

    private Boolean isAccepted = false;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}