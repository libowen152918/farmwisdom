package com.farmwisdom.service;

import com.farmwisdom.dto.AdminStatsResponse;
import com.farmwisdom.dto.UserUpdateRequest;

public interface AdminService {
    AdminStatsResponse getStats();
    void updateUser(Long id, UserUpdateRequest request);
    void resetUserPassword(Long id);
}
