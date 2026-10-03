package com.hotel.pms.api;

import com.hotel.pms.model.Room;
import com.hotel.pms.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomRepository roomRepository;

    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public List<Room> listRooms() {
        return roomRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoom(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomRepository.save(room);
    }

    /**
     * Mark a room as clean (available).
     */
    @PostMapping("/{id}/clean")
    public ResponseEntity<Room> markClean(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setStatus(Room.RoomStatus.AVAILABLE);
                    roomRepository.save(room);
                    return ResponseEntity.ok(room);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mark a room as out‑of‑order.
     */
    @PostMapping("/{id}/out-of-order")
    public ResponseEntity<Room> markOutOfOrder(@PathVariable Long id) {
        return roomRepository.findById(id)
                .map(room -> {
                    room.setStatus(Room.RoomStatus.OUT_OF_ORDER);
                    roomRepository.save(room);
                    return ResponseEntity.ok(room);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
