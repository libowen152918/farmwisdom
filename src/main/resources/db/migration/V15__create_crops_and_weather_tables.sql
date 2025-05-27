-- 创建作物表
CREATE TABLE IF NOT EXISTS crops (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '作物名称',
    category VARCHAR(50) NOT NULL COMMENT '作物类别',
    planting_season VARCHAR(50) NOT NULL COMMENT '种植季节',
    growth_cycle INT NOT NULL COMMENT '生长周期(天)',
    yield DOUBLE COMMENT '预计产量',
    description TEXT COMMENT '描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='作物信息表';

-- 创建天气表
CREATE TABLE IF NOT EXISTS weather (
    id BIGINT NOT NULL AUTO_INCREMENT,
    location VARCHAR(100) NOT NULL COMMENT '地点',
    temperature DOUBLE NOT NULL COMMENT '温度',
    humidity DOUBLE NOT NULL COMMENT '湿度',
    weather_condition VARCHAR(50) NOT NULL COMMENT '天气状况',
    record_time DATETIME NOT NULL COMMENT '记录时间',
    description TEXT COMMENT '描述',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='天气信息表';

-- 插入一些示例作物数据
INSERT INTO crops (name, category, planting_season, growth_cycle, yield, description) VALUES
('水稻', '粮食作物', '春季', 120, 600.0, '主要粮食作物，适合在温暖湿润的环境种植'),
('小麦', '粮食作物', '秋季', 180, 450.0, '耐寒作物，适合在温带地区种植'),
('玉米', '粮食作物', '春季', 90, 800.0, '高产作物，适合在温暖地区种植'),
('大豆', '经济作物', '春季', 100, 300.0, '重要的油料作物，富含蛋白质'),
('棉花', '经济作物', '春季', 150, 250.0, '重要的纺织原料作物');

-- 插入一些示例天气数据
INSERT INTO weather (location, temperature, humidity, weather_condition, record_time, description) VALUES
('北京', 25.5, 65.0, '晴', NOW(), '天气晴朗，适合户外活动'),
('上海', 28.0, 75.0, '多云', NOW(), '天气闷热，注意防暑'),
('广州', 30.5, 80.0, '雨', NOW(), '有阵雨，出门带伞'),
('成都', 26.0, 70.0, '阴', NOW(), '天气阴沉，注意保暖'),
('武汉', 27.5, 72.0, '晴', NOW(), '天气晴朗，适合晾晒'); 