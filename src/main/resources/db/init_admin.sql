-- 添加超级管理员账号
-- 密码123的BCrypt加密值
INSERT INTO Users (username, password, role, email, phoneNumber, createdAt)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EHsM8',  -- 密码123的BCrypt加密
    3,  -- 3表示超级管理员
    'admin@example.com',
    '13800138000',
    NOW()
); 