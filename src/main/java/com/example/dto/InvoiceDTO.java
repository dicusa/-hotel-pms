package com.example.dto;

import com.example.model.Invoice;

public class InvoiceDTO {
    private Long id;
    private Long reservationId;
    private double roomCharges;
    private double taxAmount;
    private double extraCharges;
    private double totalAmount;
    private double paidAmount;

    // getters & setters omitted for brevity

    public static InvoiceDTO from(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setReservationId(invoice.getReservation().getId());
        dto.setRoomCharges(invoice.getRoomCharges());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setExtraCharges(invoice.getExtraCharges());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setPaidAmount(invoice.getPaidAmount() != null ? invoice.getPaidAmount() : 0.0);
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public double getRoomCharges() { return roomCharges; }
    public void setRoomCharges(double roomCharges) { this.roomCharges = roomCharges; }
    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }
    public double getExtraCharges() { return extraCharges; }
    public void setExtraCharges(double extraCharges) { this.extraCharges = extraCharges; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }
}
