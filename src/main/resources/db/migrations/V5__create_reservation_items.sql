CREATE TABLE IF NOT EXISTS reservation_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    description VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_reservation_item_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);
