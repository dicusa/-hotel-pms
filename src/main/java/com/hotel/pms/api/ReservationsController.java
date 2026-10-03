package com.hotel.pms.api;

import com.hotel.pms.model.Guest;
import com.hotel.pms.model.Reservation;
import com.hotel.pms.model.Room;
import com.hotel.pms.repository.GuestRepository;
import com.hotel.pms.repository.ReservationRepository;
import com.hotel.pms.repository.RoomRepository;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationsController {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public ReservationsController(ReservationRepository reservationRepository, RoomRepository roomRepository, GuestRepository guestRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody ReservationRequest reservationRequest) {
        Room room = roomRepository.findById(reservationRequest.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        Guest guest = guestRepository.findById(reservationRequest.getGuestId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Guest not found"));

        if (reservationRequest.getCheckInDate().isAfter(reservationRequest.getCheckOutDate()) || reservationRequest.getCheckInDate().isEqual(reservationRequest.getCheckOutDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out date must be after check-in date");
        }

        if (isRoomBooked(room.getId(), reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate(), null)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for the selected dates");
        }

        long numberOfNights = ChronoUnit.DAYS.between(reservationRequest.getCheckInDate(), reservationRequest.getCheckOutDate());
        BigDecimal totalAmount = room.getNightlyRate().multiply(BigDecimal.valueOf(numberOfNights));

        Reservation reservation = new Reservation(
                room,
                guest,
                reservationRequest.getCheckInDate(),
                reservationRequest.getCheckOutDate(),
                totalAmount
        );
        reservation.setStatus(Reservation.ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);
        return new ResponseEntity<>(savedReservation, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'HOUSEKEEPING')")
    public List<Reservation> getReservations(
            @RequestParam(required = false) Reservation.ReservationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDateStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDateEnd,
            @RequestParam(required = false) Long roomId) {

        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .filter(r -> status == null || r.getStatus().equals(status))
                .filter(r -> checkInDateStart == null || !r.getCheckInDate().isBefore(checkInDateStart))
                .filter(r -> checkInDateEnd == null || !r.getCheckInDate().isAfter(checkInDateEnd))
                .filter(r -> roomId == null || r.getRoom().getId().equals(roomId))
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'HOUSEKEEPING')")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        return reservationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Reservation> updateReservation(@PathVariable Long id, @RequestBody ReservationUpdateRequest updateRequest) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        Room newRoom = Optional.ofNullable(updateRequest.getRoomId())
                .map(roomId -> roomRepository.findById(roomId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "New room not found")))
                .orElse(reservation.getRoom());

        LocalDate newCheckInDate = Optional.ofNullable(updateRequest.getCheckInDate()).orElse(reservation.getCheckInDate());
        LocalDate newCheckOutDate = Optional.ofNullable(updateRequest.getCheckOutDate()).orElse(reservation.getCheckOutDate());

        if (newCheckInDate.isAfter(newCheckOutDate) || newCheckInDate.isEqual(newCheckOutDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out date must be after check-in date");
        }

        // Check for conflicts with the new dates/room, excluding the current reservation
        if (isRoomBooked(newRoom.getId(), newCheckInDate, newCheckOutDate, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for the selected dates");
        }

        reservation.setRoom(newRoom);
        reservation.setCheckInDate(newCheckInDate);
        reservation.setCheckOutDate(newCheckOutDate);

        long numberOfNights = ChronoUnit.DAYS.between(reservation.getCheckInDate(), reservation.getCheckOutDate());
        BigDecimal totalAmount = reservation.getRoom().getNightlyRate().multiply(BigDecimal.valueOf(numberOfNights));
        reservation.setTotalAmount(totalAmount);

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(updatedReservation);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

        if (reservation.getStatus() == Reservation.ReservationStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a checked-in reservation");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return ResponseEntity.noContent().build();
    }

    private boolean isRoomBooked(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Long currentReservationId) {
        List<Reservation> conflictingReservations = reservationRepository.findConflictingReservations(
                roomId, checkInDate, checkOutDate
        );

        return conflictingReservations.stream()
                .anyMatch(r -> !r.getId().equals(currentReservationId) && r.getStatus() != Reservation.ReservationStatus.CANCELLED);
    }
}

// DTO for creating a reservation
class ReservationRequest {
    @NotNull(message = "Room ID cannot be null")
    private Long roomId;
    @NotNull(message = "Guest ID cannot be null")
    private Long guestId;
    @NotNull(message = "Check-in date cannot be null")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;
    @NotNull(message = "Check-out date cannot be null")
    @FutureOrPresent(message = "Check-out date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public Long getGuestId() { return guestId; }
    public void setGuestId(Long guestId) { this.guestId = guestId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
}

// DTO for updating a reservation
class ReservationUpdateRequest {
    private Long roomId;
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;
    @FutureOrPresent(message = "Check-out date must be today or in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkOutDate;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
}
