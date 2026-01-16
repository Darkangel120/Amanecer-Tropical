package com.AmanecerTropical.controller;

import com.AmanecerTropical.dto.RoomDTO;
import com.AmanecerTropical.entity.Room;
import com.AmanecerTropical.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/hotel/{hotelId}/available")
    public List<RoomDTO> getAvailableRoomsByHotelId(@PathVariable Long hotelId) {
        return roomService.getAvailableRoomsByHotel(hotelId);
    }

    @GetMapping("/hotel/{hotelId}/min-price")
    public ResponseEntity<BigDecimal> getMinPriceByHotelId(@PathVariable Long hotelId) {
        BigDecimal minPrice = roomService.getMinPriceByHotel(hotelId);
        return ResponseEntity.ok(minPrice);
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomService.saveRoom(room);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody Room roomDetails) {
        Optional<Room> room = roomService.getRoomById(id);
        if (room.isPresent()) {
            Room updatedRoom = room.get();
            updatedRoom.setNumeroHabitacion(roomDetails.getNumeroHabitacion());
            updatedRoom.setTipoHabitacion(roomDetails.getTipoHabitacion());
            updatedRoom.setCapacidad(roomDetails.getCapacidad());
            updatedRoom.setPrecioPorNoche(roomDetails.getPrecioPorNoche());
            updatedRoom.setComodidades(roomDetails.getComodidades());
            updatedRoom.setDisponible(roomDetails.isDisponible());
            updatedRoom.setActivo(roomDetails.isActivo());
            return ResponseEntity.ok(roomService.saveRoom(updatedRoom));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        if (roomService.getRoomById(id).isPresent()) {
            roomService.deleteRoom(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
