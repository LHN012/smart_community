-- 插入测试用的房屋数据（如果不存在）
INSERT IGNORE INTO areas (area_id, name) VALUES (1, '测试区域');
INSERT IGNORE INTO buildings (building_id, name) VALUES (1, 'A栋');
INSERT IGNORE INTO units (unit_id, name) VALUES (1, '1单元');
INSERT IGNORE INTO houses (house_id, area_id, building_id, unit_id, name, price, size, balance)
VALUES (1, 1, 1, 1, '101', 1000000, 100, 0);

-- 插入设备数据
INSERT INTO devices (device_id, type, device_number, house_id, install_location, status, model, manufacturer, install_time)
VALUES 
(1, '燃气', 'GAS-2025001', 1, '厨房', '正常', 'NB-IoT智能燃气表', '智能燃气表制造商', NOW()),
(2, '燃气', 'GAS-2025002', 1, '阳台', '正常', 'NB-IoT智能燃气表', '智能燃气表制造商', NOW());

-- 插入用户-房屋关系数据（假设用户ID为1是测试用户）
INSERT IGNORE INTO userhouses (user_id, house_id, relation_type)
VALUES (1, 1, '户主'); 