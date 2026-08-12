-- Criar tabela de itens de mídia
CREATE TABLE item_media (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author_director VARCHAR(255),
    release_year INT,
    genre VARCHAR(100),
    synopsis TEXT,
    media_type VARCHAR(20) NOT NULL,
    poster_url VARCHAR(500),
    external_id VARCHAR(50),
    rating INT CHECK (rating >= 0 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (title),
    INDEX idx_author_director (author_director)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
