package com.hotel.pms.api;

import com.hotel.pms.model.Room;
import com.hotel.pms.repository.RoomRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final RoomRepository roomRepository;

    public DashboardController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * Returns a simple count of rooms that are ready (AVAILABLE) vs pending cleaning (NEEDS_CLEANING)
     * for the supplied date (date is currently unused but kept for future extensions).
     */
    @GetMapping("/rooms")
    public Map<String, Integer> getRoomStatusSummary(@RequestParam(required = false) String date) {
        // date parameter kept for compatibility with the spec; not needed for current logic.
        if (date != null) {
            try {
                LocalDate.parse(date);
            } catch (Exception e) {
                // ignore parsing errors – the counts are independent of the date.
            }
        }
        int ready = (int) roomRepository.findAll().stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE)
                .count();
        int pending = (int) roomRepository.findAll().stream()
                .filter(r -> r.getStatus() == Room.RoomStatus.NEEDS_CLEANING)
                .count();
        Map<String, Integer> result = new HashMap<>();
        result.put("ready", ready);
        result.put("pending", pending);
        return result;
    }
}
