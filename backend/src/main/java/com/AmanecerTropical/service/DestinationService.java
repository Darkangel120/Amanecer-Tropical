package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Destination;
import com.AmanecerTropical.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DestinationService {

    @Autowired
    private DestinationRepository destinationRepository;

    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    public Optional<Destination> getDestinationById(Long id) {
        return destinationRepository.findById(id);
    }

    public List<Destination> getDestinationsByName(String name) {
        return destinationRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Destination> getDestinationsByLocation(String location) {
        return destinationRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Destination> getDestinationsByPriceRange(double minPrice, double maxPrice) {
        return destinationRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Destination> getAvailableDestinations() {
        return destinationRepository.findByActiveTrue();
    }

    public Destination createDestination(Destination destination) {
        return destinationRepository.save(destination);
    }

    public Destination updateDestination(Destination destination) {
        return destinationRepository.save(destination);
    }

    public void deleteDestination(Long id) {
        destinationRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return destinationRepository.existsById(id);
    }

    public List<Destination> getDestinationsByCategory(String category) {
        return destinationRepository.findByCategory(category);
    }
}
