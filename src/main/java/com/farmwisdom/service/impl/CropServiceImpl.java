package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.farmwisdom.entity.Crop;
import com.farmwisdom.mapper.CropMapper;
import com.farmwisdom.service.CropService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CropServiceImpl extends ServiceImpl<CropMapper, Crop> implements CropService {
} 