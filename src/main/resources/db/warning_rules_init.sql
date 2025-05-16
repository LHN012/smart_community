-- 清空现有数据
TRUNCATE TABLE warning_rules;

-- 插入全局默认规则
INSERT INTO warning_rules 
(rule_name, device_id, rule_type, target_field, threshold_high, threshold_low, mutation_rate, time_window, enable_status, create_time, update_time)
VALUES
-- 压力阈值规则（标准工作压力范围：2.0-2.5kPa）
('全局压力监测规则', 0, '压力阈值', 'pressure', 2.8, 1.8, NULL, 24, 1, NOW(), NOW()),

-- 流量突变规则（突变率阈值50%）
('全局流量突变监测规则', 0, '流量突变', 'flow_rate', NULL, NULL, 0.50, 24, 1, NOW(), NOW()),

-- 温度异常规则（正常温度范围：-10°C 到 40°C）
('全局温度监测规则', 0, '温度异常', 'temperature', 40.0, -10.0, NULL, 24, 1, NOW(), NOW()),

-- 长期微小流量规则（流量小于0.1m³/h持续超过时间窗口的80%）
('全局微小流量监测规则', 0, '长期微小流量', 'flow_rate', NULL, 0.1, NULL, 24, 1, NOW(), NOW());

-- 插入特定设备的规则（假设设备ID为1的是高危区域的设备，需要更严格的监控）
INSERT INTO warning_rules 
(rule_name, device_id, rule_type, target_field, threshold_high, threshold_low, mutation_rate, time_window, enable_status, create_time, update_time)
VALUES
-- 更严格的压力阈值规则
('高危区域压力监测规则', 1, '压力阈值', 'pressure', 2.6, 2.0, NULL, 24, 1, NOW(), NOW()),

-- 更敏感的流量突变规则（突变率阈值30%）
('高危区域流量突变监测规则', 1, '流量突变', 'flow_rate', NULL, NULL, 0.30, 24, 1, NOW(), NOW()),

-- 更严格的温度异常规则
('高危区域温度监测规则', 1, '温度异常', 'temperature', 35.0, -5.0, NULL, 24, 1, NOW(), NOW()),

-- 更敏感的长期微小流量规则
('高危区域微小流量监测规则', 1, '长期微小流量', 'flow_rate', NULL, 0.05, NULL, 12, 1, NOW(), NOW());

-- 插入特定设备的规则（假设设备ID为2的是普通住宅区的设备，可以采用较宽松的监控）
INSERT INTO warning_rules 
(rule_name, device_id, rule_type, target_field, threshold_high, threshold_low, mutation_rate, time_window, enable_status, create_time, update_time)
VALUES
-- 较宽松的压力阈值规则
('住宅区压力监测规则', 2, '压力阈值', 'pressure', 3.0, 1.5, NULL, 24, 1, NOW(), NOW()),

-- 较宽松的流量突变规则（突变率阈值70%）
('住宅区流量突变监测规则', 2, '流量突变', 'flow_rate', NULL, NULL, 0.70, 24, 1, NOW(), NOW()),

-- 较宽松的温度异常规则
('住宅区温度监测规则', 2, '温度异常', 'temperature', 45.0, -15.0, NULL, 24, 1, NOW(), NOW()),

-- 较宽松的长期微小流量规则
('住宅区微小流量监测规则', 2, '长期微小流量', 'flow_rate', NULL, 0.15, NULL, 36, 1, NOW(), NOW()); 