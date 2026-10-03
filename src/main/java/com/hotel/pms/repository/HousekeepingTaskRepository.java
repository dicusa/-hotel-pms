package com.hotel.pms.repository;

import com.hotel.pms.model.HousekeepingTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    List<HousekeepingTask> findByRoomId(Long roomId);
    List<HousekeepingTask> findByAssignedToId(Long userId);
}
