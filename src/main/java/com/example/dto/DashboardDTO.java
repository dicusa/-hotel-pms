package com.example.dto;

import java.util.List;

public class DashboardDTO {
    private double occupancyRate;
    private List<DailyRevenueDTO> dailyRevenue;
    private List<BookingTrendDTO> bookingTrends;

    public DashboardDTO() {}

    public DashboardDTO(double occupancyRate, List<DailyRevenueDTO> dailyRevenue, List<BookingTrendDTO> bookingTrends) {
        this.occupancyRate = occupancyRate;
        this.dailyRevenue = dailyRevenue;
        this.bookingTrends = bookingTrends;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public List<DailyRevenueDTO> getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(List<DailyRevenueDTO> dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }

    public List<BookingTrendDTO> getBookingTrends() {
        return bookingTrends;
    }

    public void setBookingTrends(List<BookingTrendDTO> bookingTrends) {
        this.bookingTrends = bookingTrends;
    }
}
