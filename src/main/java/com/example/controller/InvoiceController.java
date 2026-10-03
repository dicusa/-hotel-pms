package com.example.controller;

import com.example.dto.InvoiceDTO;
import com.example.model.Invoice;
import com.example.model.Payment;
import com.example.service.InvoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Generate an invoice for a completed reservation.
     */
    @PostMapping("/generate")
    public ResponseEntity<InvoiceDTO> generateInvoice(@RequestParam Long reservationId) {
        Invoice invoice = invoiceService.generateInvoice(reservationId);
        InvoiceDTO dto = InvoiceDTO.from(invoice);
        return ResponseEntity.ok(dto);
    }

    /**
     * Download the generated invoice as a PDF.
     */
    @GetMapping("/{invoiceId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long invoiceId) {
        byte[] pdfBytes = invoiceService.generatePdf(invoiceId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + invoiceId + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    /**
     * Record a payment against an invoice.
     */
    @PostMapping("/{invoiceId}/payments")
    public ResponseEntity<InvoiceDTO> addPayment(@PathVariable Long invoiceId, @RequestBody Payment payment) {
        Invoice updated = invoiceService.recordPayment(invoiceId, payment);
        return ResponseEntity.ok(InvoiceDTO.from(updated));
    }

    /**
     * List all payments for an invoice (optional helper).
     */
    @GetMapping("/{invoiceId}/payments")
    public ResponseEntity<List<Payment>> listPayments(@PathVariable Long invoiceId) {
        List<Payment> payments = invoiceService.getPayments(invoiceId);
        return ResponseEntity.ok(payments);
    }
}
