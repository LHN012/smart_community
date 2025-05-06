-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role INT NOT NULL DEFAULT 1 COMMENT '1:普通用户 2:管理员 3:超级管理员',
    email VARCHAR(100),
    phone_number VARCHAR(20),
    real_name VARCHAR(50) COMMENT '真实姓名',
    address VARCHAR(200) COMMENT '住址',
    avatar VARCHAR(255) COMMENT '头像URL',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建区域表
CREATE TABLE IF NOT EXISTS areas (
    area_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='区域表';

-- 创建楼栋表
CREATE TABLE IF NOT EXISTS buildings (
    building_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- 创建单元表
CREATE TABLE IF NOT EXISTS units (
    unit_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='单元表';

-- 创建房屋表
CREATE TABLE IF NOT EXISTS houses (
    house_id INT PRIMARY KEY AUTO_INCREMENT,
    area_id INT NOT NULL,
    building_id INT NOT NULL,
    unit_id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    price DOUBLE NOT NULL,
    size DOUBLE NOT NULL,
    balance DOUBLE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (area_id) REFERENCES areas(area_id),
    FOREIGN KEY (building_id) REFERENCES buildings(building_id),
    FOREIGN KEY (unit_id) REFERENCES units(unit_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房屋表';

-- 删除用户档案表
DROP TABLE IF EXISTS user_profile;

-- 微信用户表
CREATE TABLE IF NOT EXISTS wx_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    openid VARCHAR(64) NOT NULL COMMENT '微信用户唯一标识',
    unionid VARCHAR(64) COMMENT '微信开放平台唯一标识',
    session_key VARCHAR(64) COMMENT '会话密钥',
    nickname VARCHAR(64) COMMENT '微信昵称',
    avatar_url VARCHAR(255) COMMENT '微信头像URL',
    gender TINYINT COMMENT '性别：0-未知，1-男，2-女',
    country VARCHAR(64) COMMENT '国家',
    province VARCHAR(64) COMMENT '省份',
    city VARCHAR(64) COMMENT '城市',
    language VARCHAR(32) COMMENT '语言',
    phone VARCHAR(20) COMMENT '手机号',
    user_id BIGINT COMMENT '关联的用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_openid (openid),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户表'; 