package com.hotel.pms.service;

import com.hotel.pms.model.HousekeepingTask;
import com.hotel.pms.model.Reservation;
import com.hotel.pms.model.Room;
import com.hotel.pms.repository.HousekeepingTaskRepository;
import com.hotel.pms.repository.ReservationRepository;
import com.hotel.pms.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final HousekeepingTaskRepository taskRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              RoomRepository roomRepository,
                              HousekeepingTaskRepository taskRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Perform checkout for a reservation.
     * Sets reservation status to CHECKED_OUT, updates the room status to NEEDS_CLEANING,
     * and creates a pending housekeeping task.
     */
    @Transactional
    public Reservation checkOut(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        reservation.setStatus(Reservation.ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);

        Room room = reservation.getRoom();
        room.setStatus(Room.RoomStatus.NEEDS_CLEANING);
        roomRepository.save(room);

        HousekeepingTask task = new HousekeepingTask(
                room,
                null,
                "Clean room " + room.getRoomNumber(),
                LocalDate.now()
        );
        taskRepository.save(task);

        return reservation;
    }
}
