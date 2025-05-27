package com.farmwisdom.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("weather")
public class Weather {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String location;
    private Double temperature;
    private Double humidity;
    
    @TableField("weather_condition")
    private String weatherCondition;
    
    @TableField("record_time")
    private LocalDateTime recordTime;
    
    private String description;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
} 