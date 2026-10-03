package com.example.service;

import com.example.model.Invoice;
import com.example.model.Reservation;
import com.example.repository.InvoiceRepository;
import com.example.repository.PaymentRepository;
import com.example.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCheckInDate(LocalDate.of(2023, 5, 1));
        reservation.setCheckOutDate(LocalDate.of(2023, 5, 4)); // 3 nights
        reservation.setRoomRate(100.0);
        reservation.setTax(30.0); // flat tax amount
        reservation.setExtraCharges(20.0);
    }

    @Test
    void generateInvoice_calculatesCorrectTotals() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        Invoice invoice = invoiceService.generateInvoice(1L);

        assertEquals(300.0, invoice.getRoomCharges(), 0.001); // 100 * 3 nights
        assertEquals(30.0, invoice.getTaxAmount(), 0.001);
        assertEquals(20.0, invoice.getExtraCharges(), 0.001);
        assertEquals(350.0, invoice.getTotalAmount(), 0.001);
    }
}
