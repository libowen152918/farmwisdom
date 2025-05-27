package com.farmwisdom.enums;

public enum PostType {
    ARTICLE("文章"),
    QUESTION("问答"),
    LECTURE("讲座"),
    GUIDE("指南"),
    NEWS("新闻");

    private final String description;

    PostType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}