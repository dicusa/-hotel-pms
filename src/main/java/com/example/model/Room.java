package com.example.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    private String type;

    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    public Room() {}

    public Room(String number, String type, BigDecimal price) {
        this.number = number;
        this.type = type;
        this.price = price;
    }

    // getters and setters
    public Long getId() { return id; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }
}