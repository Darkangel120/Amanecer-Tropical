package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Hotel;
import com.AmanecerTropical.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public List<Hotel> getAllActiveHotels() {
        return hotelRepository.findByActivoTrue();
    }

    public List<Hotel> searchHotelsByUbicacion(@NonNull String ubicacion) {
        return hotelRepository.findByUbicacion(ubicacion);
    }

    public Optional<Hotel> getHotelById(@NonNull Long id) {
        return hotelRepository.findById(id);
    }

    public Hotel saveHotel(@NonNull Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public void deleteHotel(@NonNull Long id) {
        hotelRepository.deleteById(id);
    }
}