package com.example.dto;

import java.time.LocalDate;

public class DailyRevenueDTO {
    private LocalDate date;
    private double revenue;

    public DailyRevenueDTO() {}

    public DailyRevenueDTO(LocalDate date, double revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
}
