package com.farmwisdom.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("crops")
public class Crop {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private String category;
    private String plantingSeason;
    private Integer growthCycle;
    private Double yield;
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 