ALTER TABLE reservations
ADD COLUMN guest_id BIGINT NOT NULL DEFAULT 1,
ADD COLUMN total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE reservations
ADD CONSTRAINT fk_reservation_guest FOREIGN KEY (guest_id) REFERENCES guests(id);

-- Remove the old guest_name column if it exists and is no longer needed
-- ALTER TABLE reservations DROP COLUMN guest_name;
