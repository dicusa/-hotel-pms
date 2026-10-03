package com.hotel.pms;

import com.hotel.pms.model.HousekeepingTask;
import com.hotel.pms.model.Reservation;
import com.hotel.pms.model.Room;
import com.hotel.pms.repository.HousekeepingTaskRepository;
import com.hotel.pms.repository.ReservationRepository;
import com.hotel.pms.repository.RoomRepository;
import com.hotel.pms.service.ReservationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@Transactional
public class HousekeepingIntegrationTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private HousekeepingTaskRepository taskRepository;

    @Autowired
    private ReservationService reservationService;

    private Room room;
    private Reservation reservation;

    @BeforeEach
    public void setUp() {
        room = new Room("101", "Deluxe", new BigDecimal("120.00"));
        room = roomRepository.save(room);

        reservation = new Reservation(
                room,
                null, // guest not needed for this test
                LocalDate.now().minusDays(2),
                LocalDate.now(),
                new BigDecimal("240.00")
        );
        reservation.setStatus(Reservation.ReservationStatus.CHECKED_IN);
        reservation = reservationRepository.save(reservation);
    }

    @Test
    public void testCheckoutCreatesHousekeepingTaskAndUpdatesRoomStatus() {
        reservationService.checkOut(reservation.getId());

        Room refreshedRoom = roomRepository.findById(room.getId()).orElseThrow();
        Assertions.assertEquals(Room.RoomStatus.NEEDS_CLEANING, refreshedRoom.getStatus(), "Room status should be NEEDS_CLEANING after checkout");

        // Verify a housekeeping task exists for the room and is pending
        var tasks = taskRepository.findByRoomId(room.getId());
        Assertions.assertFalse(tasks.isEmpty(), "At least one housekeeping task should be created");
        HousekeepingTask task = tasks.get(0);
        Assertions.assertEquals("Clean room " + room.getRoomNumber(), task.getDescription());
        Assertions.assertEquals(HousekeepingTask.TaskStatus.PENDING, task.getStatus());
    }
}
