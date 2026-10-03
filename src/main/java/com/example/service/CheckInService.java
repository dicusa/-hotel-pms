package com.example.service;

import com.example.exception.ReservationException;
import com.example.model.Reservation;
import com.example.model.Room;
import com.example.repository.ReservationRepository;
import com.example.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CheckInService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public CheckInService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public Reservation checkIn(Long reservationId) throws ReservationException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("Reservation not found"));
        Room room = reservation.getRoom();
        if (room == null) {
            throw new ReservationException("Reservation has no assigned room");
        }
        // Update room status to Occupied
        room.setStatus("Occupied");
        roomRepository.save(room);
        // Mark reservation as checked‑in
        reservation.setStatus("CheckedIn");
        reservationRepository.save(reservation);
        return reservation;
    }
}
