package com.farmwisdom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.entity.Weather;
import com.farmwisdom.mapper.WeatherMapper;
import com.farmwisdom.service.WeatherService;
import com.farmwisdom.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final WeatherMapper weatherMapper;

    @Override
    public Page<Weather> getWeatherList(Integer page, Integer size, String location) {
        log.debug("Getting weather list with page: {}, size: {}, location: {}", page, size, location);
        LambdaQueryWrapper<Weather> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(location)) {
            wrapper.like(Weather::getLocation, location);
        }
        wrapper.orderByDesc(Weather::getRecordTime);
        return weatherMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    @Transactional
    public Weather createWeather(Weather weather) {
        log.debug("Creating new weather record: {}", weather);
        weatherMapper.insert(weather);
        return weather;
    }

    @Override
    @Transactional
    public Weather updateWeather(Weather weather) {
        log.debug("Updating weather record with id: {}", weather.getId());
        if (weatherMapper.updateById(weather) == 0) {
            throw new ResourceNotFoundException("Weather", "id", weather.getId());
        }
        return weather;
    }

    @Override
    @Transactional
    public void deleteWeather(Long id) {
        log.debug("Deleting weather record with id: {}", id);
        if (weatherMapper.deleteById(id) == 0) {
            throw new ResourceNotFoundException("Weather", "id", id);
        }
    }

    @Override
    public Weather getWeatherById(Long id) {
        log.debug("Getting weather record with id: {}", id);
        Weather weather = weatherMapper.selectById(id);
        if (weather == null) {
            throw new ResourceNotFoundException("Weather", "id", id);
        }
        return weather;
    }
} 