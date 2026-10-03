package com.hotel.pms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {

    public enum RoomStatus {
        AVAILABLE,
        OCCUPIED,
        CLEANING,
        MAINTENANCE,
        NEEDS_CLEANING,
        OUT_OF_ORDER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number cannot be empty")
    @Size(max = 20, message = "Room number cannot exceed 20 characters")
    @Column(nullable = false, unique = true)
    private String roomNumber;

    @NotBlank(message = "Room type cannot be empty")
    @Size(max = 50, message = "Room type cannot exceed 50 characters")
    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @NotNull(message = "Nightly rate cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Nightly rate must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal nightlyRate;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HousekeepingTask> housekeepingTasks = new ArrayList<>();

    protected Room() {}

    public Room(String roomNumber, String type, BigDecimal nightlyRate) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.nightlyRate = nightlyRate;
    }

    public Long getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }
    public BigDecimal getNightlyRate() { return nightlyRate; }
    public void setNightlyRate(BigDecimal nightlyRate) { this.nightlyRate = nightlyRate; }
    public List<Reservation> getReservations() { return reservations; }
    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
    public List<HousekeepingTask> getHousekeepingTasks() { return housekeepingTasks; }
    public void setHousekeepingTasks(List<HousekeepingTask> housekeepingTasks) { this.housekeepingTasks = housekeepingTasks; }
}
