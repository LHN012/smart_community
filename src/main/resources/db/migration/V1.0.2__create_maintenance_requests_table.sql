CREATE TABLE IF NOT EXISTS maintenance_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    location VARCHAR(200) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    images VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4; 