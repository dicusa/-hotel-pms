package com.example.controller;

import com.example.dto.ReservationDTO;
import com.example.model.Room;
import com.example.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    private Long roomId;

    @BeforeEach
    void setUp() {
        // Ensure a room exists for testing
        Room room = new Room();
        room.setNumber("101");
        room.setType("Single");
        room.setPrice(100.0);
        room = roomRepository.save(room);
        roomId = room.getId();
    }

    @Test
    void createReservation_success() throws Exception {
        ReservationDTO dto = new ReservationDTO();
        dto.setRoomId(roomId);
        dto.setGuestId(1L);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createReservation_conflict() throws Exception {
        // First reservation
        ReservationDTO first = new ReservationDTO();
        first.setRoomId(roomId);
        first.setGuestId(1L);
        first.setStartDate(LocalDate.now().plusDays(5));
        first.setEndDate(LocalDate.now().plusDays(7));
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        // Overlapping reservation
        ReservationDTO overlap = new ReservationDTO();
        overlap.setRoomId(roomId);
        overlap.setGuestId(2L);
        overlap.setStartDate(LocalDate.now().plusDays(6));
        overlap.setEndDate(LocalDate.now().plusDays(8));
        mockMvc.perform(post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(overlap)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Room is not available for the selected dates"));
    }
}
