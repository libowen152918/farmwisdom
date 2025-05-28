package com.farmwisdom.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class PostRequest {
    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度必须在2-100字符之间")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(min = 10, message = "内容至少需要10个字符")
    private String content;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    private Boolean isTop = false;

    private Boolean isEssence = false;
}
