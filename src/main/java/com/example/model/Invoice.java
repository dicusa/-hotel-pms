package com.example.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    private Reservation reservation;

    @Temporal(TemporalType.DATE)
    private Date issuedDate;

    private Double amount;
    private Boolean paid;

    public Invoice() {}

    public Invoice(Reservation reservation, Date issuedDate, Double amount, Boolean paid) {
        this.reservation = reservation;
        this.issuedDate = issuedDate;
        this.amount = amount;
        this.paid = paid;
    }

    public Long getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    public Date getIssuedDate() { return issuedDate; }
    public void setIssuedDate(Date issuedDate) { this.issuedDate = issuedDate; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }
}
