package com.example.controller;

import com.example.model.Reservation;
import com.example.service.CheckInService;
import com.example.service.ReservationService;
import com.example.exception.ReservationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckInController {

    private final ReservationService reservationService;
    private final CheckInService checkInService;

    @Autowired
    public CheckInController(ReservationService reservationService, CheckInService checkInService) {
        this.reservationService = reservationService;
        this.checkInService = checkInService;
    }

    @GetMapping("/checkin")
    public String showCheckInForm() {
        return "checkin";
    }

    @PostMapping("/checkin")
    public String lookupReservation(@RequestParam("reservationId") Long reservationId, Model model) {
        try {
            Reservation reservation = reservationService.findById(reservationId);
            model.addAttribute("reservation", reservation);
            return "checkinResult";
        } catch (ReservationException e) {
            model.addAttribute("error", e.getMessage());
            return "checkin";
        }
    }

    @PostMapping("/checkin/confirm")
    public String confirmCheckIn(@RequestParam("reservationId") Long reservationId, Model model) {
        try {
            Reservation reservation = checkInService.checkIn(reservationId);
            model.addAttribute("reservation", reservation);
            return "checkinConfirmation";
        } catch (ReservationException e) {
            model.addAttribute("error", e.getMessage());
            return "checkinResult";
        }
    }
}
