package com.farmwisdom.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.common.Result;
import com.farmwisdom.entity.Crop;
import com.farmwisdom.service.CropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/crops")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CropManagementController {

    private final CropService cropService;

    @GetMapping
    public Result<Map<String, Object>> getCropList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        log.info("获取作物列表: page={}, size={}, name={}", page, size, name);
        
        Page<Crop> cropPage = new Page<>(page, size);
        LambdaQueryWrapper<Crop> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Crop::getName, name);
        }
        
        wrapper.orderByDesc(Crop::getCreateTime);
        Page<Crop> result = cropService.page(cropPage, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        log.info("查询到 {} 条作物记录", result.getTotal());
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Crop> getCropById(@PathVariable Long id) {
        log.info("获取作物信息: id={}", id);
        Crop crop = cropService.getById(id);
        if (crop == null) {
            return Result.error("作物不存在");
        }
        return Result.success(crop);
    }

    @PostMapping
    public Result<Void> createCrop(@RequestBody Crop crop) {
        log.info("创建作物: {}", crop);
        cropService.save(crop);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateCrop(@PathVariable Long id, @RequestBody Crop crop) {
        log.info("更新作物: id={}, crop={}", id, crop);
        crop.setId(id);
        crop.setUpdateTime(LocalDateTime.now());
        cropService.updateById(crop);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCrop(@PathVariable Long id) {
        log.info("删除作物: id={}", id);
        cropService.removeById(id);
        return Result.success();
    }
} 