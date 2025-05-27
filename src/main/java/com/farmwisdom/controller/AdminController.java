package com.farmwisdom.controller;

import com.farmwisdom.dto.PostResponse;
import com.farmwisdom.service.PostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.farmwisdom.dto.AdminStatsResponse;
import com.farmwisdom.service.AdminService;
import com.farmwisdom.dto.UserUpdateRequest;
import com.farmwisdom.dto.UserRoleOption;
import com.farmwisdom.enums.UserRole;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5175", maxAge = 3600)
@RequiredArgsConstructor
public class AdminController {

    private final PostService postService;
    private final AdminService adminService;

    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponse>> getAdminPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(postService.getAdminPosts(page - 1, size, search, categoryId));
    }

    @PatchMapping("/posts/{id}/status")
    public ResponseEntity<?> updatePostStatus(
            @PathVariable Long id,
            @RequestBody PostStatusRequest request) {
        postService.updatePostStatus(id, request.getStatus());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePostAsAdmin(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        adminService.updateUser(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles")
    public List<UserRoleOption> getRoleOptions() {
        return Arrays.stream(UserRole.values())
                .map(UserRoleOption::from)
                .collect(Collectors.toList());
    }

    @PostMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable Long id) {
        adminService.resetUserPassword(id);
        return ResponseEntity.ok().build();
    }
}

class PostStatusRequest {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}