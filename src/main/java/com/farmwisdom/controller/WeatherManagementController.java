package com.farmwisdom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.farmwisdom.entity.Weather;
import com.farmwisdom.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/weather")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class WeatherManagementController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<Page<Weather>> getWeatherList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String location) {
        log.debug("Getting weather list with page: {}, size: {}, location: {}", page, size, location);
        Page<Weather> weatherPage = weatherService.getWeatherList(page, size, location);
        log.debug("Weather page response: {}", weatherPage);
        return ResponseEntity.ok(weatherPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Weather> getWeatherById(@PathVariable Long id) {
        return ResponseEntity.ok(weatherService.getWeatherById(id));
    }

    @PostMapping
    public ResponseEntity<Weather> createWeather(@RequestBody Weather weather) {
        return ResponseEntity.ok(weatherService.createWeather(weather));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Weather> updateWeather(
            @PathVariable Long id,
            @RequestBody Weather weather) {
        weather.setId(id);
        return ResponseEntity.ok(weatherService.updateWeather(weather));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeather(@PathVariable Long id) {
        weatherService.deleteWeather(id);
        return ResponseEntity.ok().build();
    }
} 