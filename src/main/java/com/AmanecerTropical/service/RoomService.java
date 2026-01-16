package com.AmanecerTropical.service;

import com.AmanecerTropical.dto.RoomDTO;
import com.AmanecerTropical.entity.Room;
import com.AmanecerTropical.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    public List<RoomDTO> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotel_IdAndDisponibleTrue(hotelId);
    }

    public BigDecimal getMinPriceByHotel(Long hotelId) {
        return roomRepository.findMinPriceByHotelId(hotelId);
    }

    @SuppressWarnings("null")
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @SuppressWarnings("null")
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}