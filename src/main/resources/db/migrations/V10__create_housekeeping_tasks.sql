CREATE TABLE IF NOT EXISTS housekeeping_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    assigned_to_user_id BIGINT,
    description VARCHAR(255) NOT NULL,
    due_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes VARCHAR(500),
    CONSTRAINT fk_housekeeping_task_room FOREIGN KEY (room_id) REFERENCES rooms(id),
    CONSTRAINT fk_housekeeping_task_user FOREIGN KEY (assigned_to_user_id) REFERENCES users(id)
);
