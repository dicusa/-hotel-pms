package com.example.controller;

import com.example.model.Room;
import com.example.model.RoomStatus;
import com.example.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomRepository roomRepository;

    @Test
    public void testGetRooms() throws Exception {
        Room r1 = new Room();
        r1.setId(1L);
        r1.setNumber("101");
        r1.setFloor(1);
        r1.setStatus(RoomStatus.AVAILABLE);
        Mockito.when(roomRepository.findAll()).thenReturn(Arrays.asList(r1));

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    public void testUpdateStatus() throws Exception {
        Room r = new Room();
        r.setId(2L);
        r.setNumber("202");
        r.setFloor(2);
        r.setStatus(RoomStatus.CLEANING);
        Mockito.when(roomRepository.findById(2L)).thenReturn(Optional.of(r));
        Mockito.when(roomRepository.save(Mockito.any(Room.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(put("/api/rooms/2/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"AVAILABLE\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }
}
