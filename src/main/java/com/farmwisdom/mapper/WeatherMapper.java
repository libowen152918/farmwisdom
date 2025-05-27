package com.farmwisdom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.farmwisdom.entity.Weather;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WeatherMapper extends BaseMapper<Weather> {
} 