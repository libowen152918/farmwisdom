package com.farmwisdom.service;

import com.farmwisdom.entity.Weather;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface WeatherService {
    /**
     * 获取天气列表
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param location 地点（可选）
     * @return 天气列表分页数据
     */
    Page<Weather> getWeatherList(Integer page, Integer size, String location);

    /**
     * 创建天气记录
     * @param weather 天气信息
     * @return 创建后的天气记录
     */
    Weather createWeather(Weather weather);

    /**
     * 更新天气记录
     * @param weather 天气信息
     * @return 更新后的天气记录
     */
    Weather updateWeather(Weather weather);

    /**
     * 删除天气记录
     * @param id 天气记录ID
     */
    void deleteWeather(Long id);

    /**
     * 根据ID获取天气记录
     * @param id 天气记录ID
     * @return 天气记录
     */
    Weather getWeatherById(Long id);
} 