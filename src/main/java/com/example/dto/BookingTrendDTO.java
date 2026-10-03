package com.example.dto;

import java.time.LocalDate;

public class BookingTrendDTO {
    private LocalDate date;
    private long bookings;

    public BookingTrendDTO() {}

    public BookingTrendDTO(LocalDate date, long bookings) {
        this.date = date;
        this.bookings = bookings;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getBookings() {
        return bookings;
    }

    public void setBookings(long bookings) {
        this.bookings = bookings;
    }
}
