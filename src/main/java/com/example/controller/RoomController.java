package com.example.controller;

import com.example.model.Room;
import com.example.model.RoomStatus;
import com.example.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RoomController {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public RoomController(RoomRepository roomRepository, SimpMessagingTemplate messagingTemplate) {
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/rooms")
    public List<Room> getRooms(@RequestParam(required = false) Integer floor,
                               @RequestParam(required = false) RoomStatus status) {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .filter(r -> floor == null || r.getFloor() == floor)
                .filter(r -> status == null || r.getStatus() == status)
                .collect(Collectors.toList());
    }

    @PutMapping("/rooms/{id}/status")
    public Room updateStatus(@PathVariable Long id, @RequestBody RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setStatus(status);
        Room saved = roomRepository.save(room);
        messagingTemplate.convertAndSend("/topic/rooms", saved);
        return saved;
    }
}
