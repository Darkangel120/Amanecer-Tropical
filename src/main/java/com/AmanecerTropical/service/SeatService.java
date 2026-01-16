package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Seat;
import com.AmanecerTropical.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    @SuppressWarnings("null")
    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public List<Seat> getSeatsByVueloId(Long vueloId) {
        return seatRepository.findByVueloId(vueloId);
    }

    public List<Seat> getAvailableSeatsByVueloId(Long vueloId) {
        return seatRepository.findAvailableSeatsByVueloId(vueloId);
    }

    @SuppressWarnings("null")
    public Seat saveSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    @SuppressWarnings("null")
    public void deleteSeat(Long id) {
        seatRepository.deleteById(id);
    }

    public long countAvailableSeatsByVueloId(Long vueloId) {
        return seatRepository.countByVueloIdAndDisponible(vueloId, true);
    }
}
