package com.example.controller;

import com.example.model.Guest;
import com.example.model.Invoice;
import com.example.model.Payment;
import com.example.model.Reservation;
import com.example.model.Room;
import com.example.repository.GuestRepository;
import com.example.repository.InvoiceRepository;
import com.example.repository.PaymentRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DashboardControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        invoiceRepository.deleteAll();
        paymentRepository.deleteAll();
        roomRepository.deleteAll();
        guestRepository.deleteAll();

        Guest guest = new Guest();
        guest.setName("John Doe");
        guestRepository.save(guest);

        Room room = new Room();
        room.setNumber("101");
        room.setType("Single");
        room.setPrice(100.0);
        roomRepository.save(room);

        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setCheckInDate(LocalDate.now().minusDays(1));
        reservation.setCheckOutDate(LocalDate.now().plusDays(2));
        reservationRepository.save(reservation);

        Payment payment = new Payment();
        payment.setAmount(200.0);
        payment.setDate(LocalDate.now());
        paymentRepository.save(payment);

        Invoice invoice = new Invoice();
        invoice.setReservation(reservation);
        invoice.setPayment(payment);
        invoice.setTotalAmount(200.0);
        invoice.setPaymentDate(LocalDate.now());
        invoiceRepository.save(invoice);
    }

    @Test
    void testDashboardEndpoint() throws Exception {
        String start = LocalDate.now().minusDays(2).toString();
        String end = LocalDate.now().plusDays(2).toString();
        mockMvc.perform(get("/dashboard").param("start", start).param("end", end))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupancyRate").isNumber())
                .andExpect(jsonPath("$.dailyRevenue").isArray())
                .andExpect(jsonPath("$.bookingTrends").isArray());
    }
}
