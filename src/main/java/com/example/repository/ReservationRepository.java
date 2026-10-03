package com.example.repository;

import com.example.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT COUNT(DISTINCT r.room.id) FROM Reservation r WHERE r.checkInDate <= :end AND r.checkOutDate >= :start")
    long countDistinctRoomByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    long countByCheckInDate(LocalDate date);
}
