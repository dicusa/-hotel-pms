package com.example.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Guest guest;

    @ManyToOne(optional = false)
    private Room room;

    @Temporal(TemporalType.DATE)
    private Date checkIn;

    @Temporal(TemporalType.DATE)
    private Date checkOut;

    private Double totalAmount;

    public Reservation() {}

    public Reservation(Guest guest, Room room, Date checkIn, Date checkOut, Double totalAmount) {
        this.guest = guest;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
    }

    public Long getId() { return id; }
    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public Date getCheckIn() { return checkIn; }
    public void setCheckIn(Date checkIn) { this.checkIn = checkIn; }
    public Date getCheckOut() { return checkOut; }
    public void setCheckOut(Date checkOut) { this.checkOut = checkOut; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
}
