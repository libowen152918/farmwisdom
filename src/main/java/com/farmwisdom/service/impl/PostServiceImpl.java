package com.farmwisdom.service.impl;

import com.farmwisdom.entity.Post;
import com.farmwisdom.entity.PostRequest;
import com.farmwisdom.entity.PostResponse;
import com.farmwisdom.exception.ResourceNotFoundException;
import com.farmwisdom.mapper.PostMapper;
import com.farmwisdom.service.PostService;
import com.farmwisdom.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }

        // 检查是否是作者本人或管理员
        if (!SecurityUtils.isCurrentUserAdmin() && !post.getUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new RuntimeException("没有权限修改此帖子");
        }

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setCategoryId(postRequest.getCategoryId());

        // 只有管理员可以设置置顶和精品状态
        if (SecurityUtils.isCurrentUserAdmin()) {
            if (postRequest.getIsTop() != null) {
                post.setIsTop(postRequest.getIsTop());
            }
            if (postRequest.getIsEssence() != null) {
                post.setIsEssence(postRequest.getIsEssence());
            }
        }

        postMapper.updateById(post);
        return convertToPostResponse(post);
    }

    private PostResponse convertToPostResponse(Post post) {
        // Implementation of convertToPostResponse method
        return null; // Placeholder return, actual implementation needed
    }
} 