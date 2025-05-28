package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.dto.PostRequest;
import com.farmwisdom.dto.PostResponse;
import com.farmwisdom.entity.Post;
import com.farmwisdom.entity.Category;
import com.farmwisdom.entity.User;
import com.farmwisdom.exception.ResourceNotFoundException;
import com.farmwisdom.mapper.PostMapper;
import com.farmwisdom.mapper.CategoryMapper;
import com.farmwisdom.mapper.UserMapper;
import com.farmwisdom.security.SecurityUtils;
import com.farmwisdom.service.PostService;
import com.farmwisdom.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private static final Logger log = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostMapper postMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final UserService userService;

    public PostServiceImpl(PostMapper postMapper, CategoryMapper categoryMapper, UserMapper userMapper, UserService userService) {
        this.postMapper = postMapper;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public Page<PostResponse> getPosts(Integer page, Integer size, Long categoryId) {
        log.debug("Getting posts - page: {}, size: {}, categoryId: {}", page, size, categoryId);

        try {
            // 创建查询条件
            LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

            // 只查询已发布的帖子
            wrapper.eq(Post::getStatus, "active");

            // 如果指定了分类，则按分类查询
            if (categoryId != null) {
                wrapper.eq(Post::getCategoryId, categoryId);
            }

            // 按置顶和创建时间排序
            wrapper.orderByDesc(Post::getIsTop)
                    .orderByDesc(Post::getCreateTime);

            // 执行分页查询
            Page<Post> postPage = postMapper.selectPage(
                    new Page<>(page, size),
                    wrapper
            );

            // 转换结果
            Page<PostResponse> responsePage = new Page<>();
            responsePage.setCurrent(postPage.getCurrent());
            responsePage.setSize(postPage.getSize());
            responsePage.setTotal(postPage.getTotal());
            responsePage.setRecords(postPage.getRecords().stream()
                    .map(this::convertToPostResponse)
                    .collect(Collectors.toList()));

            log.debug("Found {} posts", responsePage.getTotal());
            return responsePage;

        } catch (Exception e) {
            log.error("Error getting posts", e);
            throw new RuntimeException("获取帖子列表失败: " + e.getMessage());
        }
    }

    @Override
    public PostResponse getPost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }

        // 增加浏览次数
        post.setViewCount(post.getViewCount() + 1);
        postMapper.updateById(post);

        return convertToPostResponse(post);
    }

    @Override
    @Transactional
    public PostResponse createPost(PostRequest postRequest) {
        Post post = new Post();
        post.setUserId(SecurityUtils.getCurrentUserId());
        post.setCategoryId(postRequest.getCategoryId());
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setIsTop(false);
        post.setIsEssence(false);
        post.setStatus("PUBLISHED");

        postMapper.insert(post);
        return convertToPostResponse(post);
    }

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

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }

        // 检查是否是作者本人或管理员
        if (!SecurityUtils.isCurrentUserAdmin() && !post.getUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new RuntimeException("没有权限删除此帖子");
        }

        postMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void likePost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }

        post.setLikeCount(post.getLikeCount() + 1);
        postMapper.updateById(post);
    }

    @Override
    @Transactional
    public void unlikePost(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }

        if (post.getLikeCount() > 0) {
            post.setLikeCount(post.getLikeCount() - 1);
            postMapper.updateById(post);
        }
    }

    @Override
    public Page<PostResponse> getAdminPosts(Integer page, Integer size, String search, Long categoryId) {
        log.debug("Getting admin posts - page: {}, size: {}, search: {}, categoryId: {}", page, size, search, categoryId);

        try {
            // 创建查询条件
            LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();

            // 添加标题搜索条件
            if (search != null && !search.trim().isEmpty()) {
                queryWrapper.like(Post::getTitle, search.trim());
            }

            // 添加分类过滤条件
            if (categoryId != null) {
                queryWrapper.eq(Post::getCategoryId, categoryId);
            }

            // 按创建时间降序排序
            queryWrapper.orderByDesc(Post::getCreateTime);

            log.debug("Executing page query with wrapper: {}", queryWrapper.getSqlSegment());

            // 执行分页查询
            Page<Post> postPage = postMapper.selectPage(new Page<>(page, size), queryWrapper);

            log.debug("Query executed. Total records: {}, Current page: {}",
                    postPage.getTotal(), postPage.getCurrent());

            // 转换为 PostResponse
            Page<PostResponse> responsePage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
            responsePage.setRecords(postPage.getRecords().stream()
                    .map(this::convertToPostResponse)
                    .collect(Collectors.toList()));

            log.debug("Successfully converted {} posts to response",
                    responsePage.getRecords().size());

            return responsePage;

        } catch (Exception e) {
            log.error("Error getting admin posts", e);
            throw new RuntimeException("获取帖子列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updatePostStatus(Long id, String status) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }

        post.setStatus(status);
        postMapper.updateById(post);
    }

    @Override
    @Transactional
    public void deletePostAsAdmin(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("Post", "id", id);
        }

        postMapper.deleteById(id);
    }

    @Override
    public Page<PostResponse> getMyPosts(Integer page, Integer size) {
        log.debug("Getting my posts - page: {}, size: {}", page, size);

        try {
            // 创建查询条件
            LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();

            // 只查询当前用户的帖子
            wrapper.eq(Post::getUserId, SecurityUtils.getCurrentUserId());

            // 按创建时间降序排序
            wrapper.orderByDesc(Post::getCreateTime);

            // 执行分页查询
            Page<Post> postPage = postMapper.selectPage(
                    new Page<>(page, size),
                    wrapper
            );

            // 转换结果
            Page<PostResponse> responsePage = new Page<>();
            responsePage.setCurrent(postPage.getCurrent());
            responsePage.setSize(postPage.getSize());
            responsePage.setTotal(postPage.getTotal());
            responsePage.setRecords(postPage.getRecords().stream()
                    .map(this::convertToPostResponse)
                    .collect(Collectors.toList()));

            log.debug("Found {} posts", responsePage.getTotal());
            return responsePage;

        } catch (Exception e) {
            log.error("Error getting my posts", e);
            throw new RuntimeException("获取我的帖子列表失败: " + e.getMessage());
        }
    }

    private PostResponse convertToPostResponse(Post post) {
        log.debug("Converting post to response. Post ID: {}", post.getId());

        try {
            PostResponse response = new PostResponse();
            response.setId(post.getId());
            response.setUserId(post.getUserId());
            response.setCategoryId(post.getCategoryId());
            response.setTitle(post.getTitle());
            response.setContent(post.getContent());
            response.setViewCount(post.getViewCount());
            response.setLikeCount(post.getLikeCount());
            response.setCommentCount(post.getCommentCount());
            response.setIsTop(post.getIsTop());
            response.setIsEssence(post.getIsEssence());
            response.setStatus(post.getStatus());
            response.setCreateTime(post.getCreateTime());
            response.setUpdateTime(post.getUpdateTime());

            // 添加分类名称
            Category category = categoryMapper.selectById(post.getCategoryId());
            if (category != null) {
                response.setCategoryName(category.getName());
            } else {
                log.warn("Category not found for post {}. Category ID: {}",
                        post.getId(), post.getCategoryId());
            }

            // 添加作者信息
            User author = userMapper.selectById(post.getUserId());
            if (author != null) {
                response.setAuthor(author.getUsername());
            } else {
                log.warn("Author not found for post {}. User ID: {}",
                        post.getId(), post.getUserId());
            }

            return response;
        } catch (Exception e) {
            log.error("Error converting post to response", e);
            throw new RuntimeException("转换帖子数据失败: " + e.getMessage());
        }
    }
} 