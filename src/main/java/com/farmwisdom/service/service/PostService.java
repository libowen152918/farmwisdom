package com.farmwisdom.service;

import com.farmwisdom.dto.PostRequest;
import com.farmwisdom.dto.PostResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface PostService {
    Page<PostResponse> getPosts(Integer page, Integer size, Long categoryId);
    Page<PostResponse> getMyPosts(Integer page, Integer size);
    PostResponse getPost(Long id);
    PostResponse createPost(PostRequest postRequest);
    PostResponse updatePost(Long id, PostRequest postRequest);
    void deletePost(Long id);
    void likePost(Long id);
    void unlikePost(Long id);

    // Admin operations
    Page<PostResponse> getAdminPosts(Integer page, Integer size, String search, Long categoryId);
    void updatePostStatus(Long id, String status);
    void deletePostAsAdmin(Long id);
}
