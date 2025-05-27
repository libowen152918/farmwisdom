package com.farmwisdom.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("attachments")
public class Attachment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private String type; // IMAGE, VIDEO, FILE

    private String url;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
