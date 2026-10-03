package com.example.service;

import com.example.dto.ReservationDTO;
import com.example.exception.ReservationException;
import com.example.model.Reservation;
import com.example.model.Room;
import com.example.repository.ReservationRepository;
import com.example.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    public ReservationService(ReservationRepository reservationRepository, RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public Reservation createReservation(ReservationDTO dto) {
        validateDates(dto.getStartDate(), dto.getEndDate());
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        checkAvailability(room.getId(), dto.getStartDate(), dto.getEndDate(), null);
        Reservation reservation = new Reservation();
        reservation.setRoom(room);
        reservation.setGuestId(dto.getGuestId()); // assuming guestId field exists
        reservation.setStartDate(dto.getStartDate());
        reservation.setEndDate(dto.getEndDate());
        return reservationRepository.save(reservation);
    }

    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
    }

    @Transactional
    public Reservation updateReservation(Long id, ReservationDTO dto) {
        validateDates(dto.getStartDate(), dto.getEndDate());
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found"));
        checkAvailability(room.getId(), dto.getStartDate(), dto.getEndDate(), id);
        existing.setRoom(room);
        existing.setGuestId(dto.getGuestId());
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        return reservationRepository.save(existing);
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation not found");
        }
        reservationRepository.deleteById(id);
    }

    public List<Reservation> listAll() {
        return reservationRepository.findAll();
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new ReservationException("Start date and end date must be provided");
        }
        if (end.isBefore(start)) {
            throw new ReservationException("End date cannot be before start date");
        }
    }

    private void checkAvailability(Long roomId, LocalDate start, LocalDate end, Long excludeReservationId) {
        List<Reservation> conflicts = reservationRepository.findByRoomIdAndDateOverlap(roomId, start, end);
        if (excludeReservationId != null) {
            conflicts = conflicts.stream()
                    .filter(r -> !r.getId().equals(excludeReservationId))
                    .collect(Collectors.toList());
        }
        if (!conflicts.isEmpty()) {
            throw new ReservationException("Room is not available for the selected dates");
        }
    }
}
