CREATE TABLE IF NOT EXISTS contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number VARCHAR(20),
    email VARCHAR(255),
    linked_id BIGINT,
    link_precedence VARCHAR(20) NOT NULL CHECK (link_precedence IN ('PRIMARY', 'SECONDARY')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (linked_id) REFERENCES contacts(id)
);

CREATE INDEX idx_email ON contacts(email);
CREATE INDEX idx_phone ON contacts(phone_number);
CREATE INDEX idx_linked_id ON contacts(linked_id);
CREATE INDEX idx_precedence ON contacts(link_precedence);
CREATE INDEX idx_created_at ON contacts(created_at);
CREATE INDEX idx_composite_search ON contacts(email, phone_number);
