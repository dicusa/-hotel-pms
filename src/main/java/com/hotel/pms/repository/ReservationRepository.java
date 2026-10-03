package com.hotel.pms.repository;

import com.hotel.pms.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByCheckInDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.status != 'CANCELLED' AND " +
           "((r.checkInDate < :checkOutDate AND r.checkOutDate > :checkInDate)) ")
    List<Reservation> findConflictingReservations(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate);
}
