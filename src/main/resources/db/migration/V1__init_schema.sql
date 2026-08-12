CREATE TABLE item_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_year INT,
    media_type VARCHAR(50) NOT NULL,
    rating DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
