package com.hotel.pms.api;

import com.hotel.pms.model.Reservation;
import com.hotel.pms.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Checkout a reservation and trigger housekeeping task creation.
     */
    @PostMapping("/{id}/checkout")
    public ResponseEntity<Reservation> checkout(@PathVariable Long id) {
        try {
            Reservation updated = reservationService.checkOut(id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
