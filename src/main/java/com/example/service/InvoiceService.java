package com.example.service;

import com.example.model.Invoice;
import com.example.model.Payment;
import com.example.model.Reservation;
import com.example.repository.InvoiceRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final ReservationRepository reservationRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public InvoiceService(ReservationRepository reservationRepository,
                          InvoiceRepository invoiceRepository,
                          PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Creates an invoice for the given reservation id.
     */
    @Transactional
    public Invoice generateInvoice(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        // Simple calculation: roomRate * nights + tax + extraCharges
        long nights = calculateNights(reservation.getCheckInDate(), reservation.getCheckOutDate());
        double roomTotal = reservation.getRoomRate() * nights;
        double taxAmount = reservation.getTax(); // assuming tax is already a monetary amount
        double extra = reservation.getExtraCharges() != null ? reservation.getExtraCharges() : 0.0;
        double total = roomTotal + taxAmount + extra;

        Invoice invoice = new Invoice();
        invoice.setReservation(reservation);
        invoice.setRoomCharges(roomTotal);
        invoice.setTaxAmount(taxAmount);
        invoice.setExtraCharges(extra);
        invoice.setTotalAmount(total);
        invoice = invoiceRepository.save(invoice);
        return invoice;
    }

    private long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        return Duration.between(checkIn.atStartOfDay(), checkOut.atStartOfDay()).toDays();
    }

    /**
     * Generates a very simple PDF representation of the invoice.
     * For demo purposes we just write plain text into a PDF‑compatible byte array.
     */
    public byte[] generatePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        StringBuilder sb = new StringBuilder();
        sb.append("Invoice #").append(invoice.getId()).append("\n");
        sb.append("Reservation ID: ").append(invoice.getReservation().getId()).append("\n");
        sb.append("Room Charges: $").append(String.format("%.2f", invoice.getRoomCharges())).append("\n");
        sb.append("Tax: $").append(String.format("%.2f", invoice.getTaxAmount())).append("\n");
        sb.append("Extra Charges: $").append(String.format("%.2f", invoice.getExtraCharges())).append("\n");
        sb.append("Total: $").append(String.format("%.2f", invoice.getTotalAmount())).append("\n");
        // Very naive PDF – just plain text bytes. Real implementation would use a PDF library.
        return sb.toString().getBytes();
    }

    @Transactional
    public Invoice recordPayment(Long invoiceId, Payment payment) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        payment.setInvoice(invoice);
        paymentRepository.save(payment);
        // Update paid amount on invoice
        double newPaid = (invoice.getPaidAmount() != null ? invoice.getPaidAmount() : 0.0) + payment.getAmount();
        invoice.setPaidAmount(newPaid);
        return invoiceRepository.save(invoice);
    }

    public List<Payment> getPayments(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        return paymentRepository.findByInvoiceId(invoice.getId());
    }
}
