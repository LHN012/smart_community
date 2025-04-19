-- 添加超级管理员账号
INSERT INTO Users (username, password, role, email, phoneNumber, createdAt)
VALUES (
    'admin',
    '123456',  -- 明文密码
    3,  -- 3表示超级管理员
    'admin@example.com',
    '13800138000',
    NOW()
); 