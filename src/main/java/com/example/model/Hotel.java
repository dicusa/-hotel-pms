package com.example.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Room> rooms = new HashSet<>();

    public Hotel() {}

    public Hotel(String name, String address) {
        this.name = name;
        this.address = address;
    }

    // getters and setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Set<Room> getRooms() { return rooms; }
    public void addRoom(Room room) {
        rooms.add(room);
        room.setHotel(this);
    }
    public void removeRoom(Room room) {
        rooms.remove(room);
        room.setHotel(null);
    }
}