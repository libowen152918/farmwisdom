package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.farmwisdom.dto.AdminStatsResponse;
import com.farmwisdom.dto.UserUpdateRequest;
import com.farmwisdom.entity.Crop;
import com.farmwisdom.entity.Post;
import com.farmwisdom.entity.User;
import com.farmwisdom.entity.Weather;
import com.farmwisdom.exception.BusinessException;
import com.farmwisdom.mapper.UserMapper;
import com.farmwisdom.mapper.PostMapper;
import com.farmwisdom.mapper.CropMapper;
import com.farmwisdom.mapper.WeatherMapper;
import com.farmwisdom.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CropMapper cropMapper;
    private final WeatherMapper weatherMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminStatsResponse getStats() {
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getDeleted, 0));
        Long postCount = postMapper.selectCount(new LambdaQueryWrapper<Post>().eq(Post::getDeleted, 0));
        Long cropCount = cropMapper.selectCount(new LambdaQueryWrapper<Crop>().eq(Crop::getDeleted, 0));
        Long weatherCount = weatherMapper.selectCount(new LambdaQueryWrapper<Weather>().eq(Weather::getDeleted, 0));

        return AdminStatsResponse.builder()
                .userCount(userCount)
                .postCount(postCount)
                .cropCount(cropCount)
                .weatherCount(weatherCount)
                .build();
    }

    @Override
    @Transactional
    public void updateUser(Long id, UserUpdateRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在", 404);
        }

        // 更新用户信息
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getRole() != null) {
            try {
                UserRole.valueOf(request.getRole());  // 验证角色是否有效
                user.setRole(request.getRole());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("无效的用户角色", 400);
            }
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getReputation() != null) {
            user.setReputation(request.getReputation());
        }

        // 执行更新
        int result = userMapper.updateById(user);
        if (result != 1) {
            throw new BusinessException("更新用户信息失败", 500);
        }
    }

    @Override
    @Transactional
    public void resetUserPassword(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在", 404);
        }

        // 警告：出于测试目的，将密码直接重置为 "123456"，请勿在生产环境中使用！
        String newPassword = "123456";
        user.setPassword(passwordEncoder.encode(newPassword)); // 对新密码进行加密

        int result = userMapper.updateById(user); // 更新用户密码
        if (result != 1) {
            throw new BusinessException("重置用户密码失败", 500);
        }

        // TODO: 考虑通过更安全的方式告知用户新密码（例如发送到注册邮箱）
    }
}