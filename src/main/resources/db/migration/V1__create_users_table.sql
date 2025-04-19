CREATE TABLE users (
    user_id INT NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    role INT NOT NULL COMMENT '用户角色: 1=普通用户, 2=管理员, 3=超级管理员',
    email VARCHAR(100) DEFAULT NULL COMMENT '电子邮件地址',
    phone_number VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    real_name VARCHAR(255) DEFAULT NULL COMMENT '真实姓名',
    address VARCHAR(255) DEFAULT NULL COMMENT '地址信息',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
    PRIMARY KEY (user_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_german2_ci COMMENT='用户表'; 