package com.example.service;

import com.example.dto.BookingTrendDTO;
import com.example.dto.DailyRevenueDTO;
import com.example.dto.DashboardDTO;
import com.example.repository.InvoiceRepository;
import com.example.repository.ReservationRepository;
import com.example.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    private final ReservationRepository reservationRepository;
    private final InvoiceRepository invoiceRepository;
    private final RoomRepository roomRepository;

    public DashboardService(ReservationRepository reservationRepository,
                            InvoiceRepository invoiceRepository,
                            RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.invoiceRepository = invoiceRepository;
        this.roomRepository = roomRepository;
    }

    public DashboardDTO buildDashboard(LocalDate start, LocalDate end) {
        double occupancyRate = calculateOccupancyRate(start, end);
        List<DailyRevenueDTO> dailyRevenue = calculateDailyRevenue(start, end);
        List<BookingTrendDTO> bookingTrends = calculateBookingTrends(start, end);
        return new DashboardDTO(occupancyRate, dailyRevenue, bookingTrends);
    }

    private double calculateOccupancyRate(LocalDate start, LocalDate end) {
        long totalRooms = roomRepository.count();
        if (totalRooms == 0) {
            return 0.0;
        }
        long occupiedRooms = reservationRepository.countDistinctRoomByDateRange(start, end);
        return ((double) occupiedRooms / totalRooms) * 100.0;
    }

    private List<DailyRevenueDTO> calculateDailyRevenue(LocalDate start, LocalDate end) {
        List<DailyRevenueDTO> list = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            double sum = invoiceRepository.sumTotalAmountByPaymentDate(date);
            list.add(new DailyRevenueDTO(date, sum));
        }
        return list;
    }

    private List<BookingTrendDTO> calculateBookingTrends(LocalDate start, LocalDate end) {
        List<BookingTrendDTO> list = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            long count = reservationRepository.countByCheckInDate(date);
            list.add(new BookingTrendDTO(date, count));
        }
        return list;
    }
}
