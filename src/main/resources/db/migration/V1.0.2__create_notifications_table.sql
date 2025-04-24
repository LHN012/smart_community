-- 创建通知表
CREATE TABLE IF NOT EXISTS notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    notification_type VARCHAR(20) NOT NULL COMMENT '通知类型',
    title VARCHAR(100) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    sender_id INT NOT NULL COMMENT '发送者ID',
    receiver_type VARCHAR(50) NOT NULL COMMENT '接收者类型：11=全体业主，12=物业员工，13=特定房间业主',
    receiver_id INT DEFAULT NULL COMMENT '接收者ID（特定房间业主时使用）',
    send_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    FOREIGN KEY (sender_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 创建通知接收者表
CREATE TABLE IF NOT EXISTS notification_receivers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    notification_id INT NOT NULL COMMENT '通知ID',
    receiver_user_id INT NOT NULL COMMENT '接收者用户ID',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    FOREIGN KEY (notification_id) REFERENCES notifications(id),
    FOREIGN KEY (receiver_user_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知接收者表'; 