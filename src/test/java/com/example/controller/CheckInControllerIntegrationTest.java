package com.example.controller;

import com.example.model.Guest;
import com.example.model.Room;
import com.example.model.Reservation;
import com.example.repository.GuestRepository;
import com.example.repository.RoomRepository;
import com.example.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CheckInControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Reservation reservation;

    @BeforeEach
    public void setUp() {
        Guest guest = new Guest();
        guest.setName("John Doe");
        guest = guestRepository.save(guest);

        Room room = new Room();
        room.setNumber("101");
        room.setStatus("Available");
        room = roomRepository.save(room);

        reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoom(room);
        reservation.setStatus("Booked");
        reservation = reservationRepository.save(reservation);
    }

    @Test
    public void fullCheckInFlow() throws Exception {
        // 1. Show the check‑in form
        mockMvc.perform(get("/checkin"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkin"));

        // 2. Lookup reservation by ID
        mockMvc.perform(post("/checkin")
                        .param("reservationId", reservation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("checkinResult"))
                .andExpect(model().attributeExists("reservation"));

        // 3. Confirm check‑in
        mockMvc.perform(post("/checkin/confirm")
                        .param("reservationId", reservation.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("checkinConfirmation"))
                .andExpect(model().attributeExists("reservation"));

        // 4. Verify room status updated
        Room updatedRoom = roomRepository.findById(reservation.getRoom().getId()).orElseThrow();
        assertThat(updatedRoom.getStatus()).isEqualTo("Occupied");
    }
}
