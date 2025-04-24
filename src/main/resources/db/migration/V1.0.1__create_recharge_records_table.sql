-- 创建充值记录表
CREATE TABLE IF NOT EXISTS recharge_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    house_id INT NOT NULL,
    user_id INT NOT NULL,
    balance_before DECIMAL(10,2) NOT NULL,
    balance_after DECIMAL(10,2) NOT NULL,
    recharge_amount DECIMAL(10,2) NOT NULL,
    recharge_channel VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    recharge_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (house_id) REFERENCES houses(house_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 创建充值记录存储过程
DELIMITER //

CREATE PROCEDURE insert_recharge_record(
    IN p_house_id INT,
    IN p_user_id INT,
    IN p_balance_before DECIMAL(10,2),
    IN p_balance_after DECIMAL(10,2),
    IN p_recharge_amount DECIMAL(10,2),
    IN p_recharge_channel VARCHAR(20),
    IN p_payment_method VARCHAR(20)
)
BEGIN
    -- 插入充值记录
    INSERT INTO recharge_records (
        house_id,
        user_id,
        balance_before,
        balance_after,
        recharge_amount,
        recharge_channel,
        payment_method,
        recharge_time
    ) VALUES (
        p_house_id,
        p_user_id,
        p_balance_before,
        p_balance_after,
        p_recharge_amount,
        p_recharge_channel,
        p_payment_method,
        NOW()
    );
    
    -- 更新房屋余额
    UPDATE houses
    SET balance = p_balance_after
    WHERE house_id = p_house_id;
END //

DELIMITER ; 