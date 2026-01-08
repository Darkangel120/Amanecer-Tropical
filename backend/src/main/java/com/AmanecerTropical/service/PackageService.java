package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.Package;
import com.AmanecerTropical.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PackageService {

    @Autowired
    private PackageRepository packageRepository;

    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    public Optional<Package> getPackageById(@NonNull Long id) {
        return packageRepository.findById(id);
    }

    public List<Package> getPackagesByNombre(String nombre) {
        return packageRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Package> getPackagesByUbicacion(String ubicacion) {
        return packageRepository.findByUbicacionContainingIgnoreCase(ubicacion);
    }

    public List<Package> getPackagesByPriceRange(double minPrice, double maxPrice) {
        return packageRepository.findByPrecioBetween(minPrice, maxPrice);
    }

    public List<Package> getAvailablePackages() {
        return packageRepository.findByActivoTrue();
    }

    public Package createPackage(@NonNull Package pkg) {
        pkg.setActivo(true);
        pkg.setPopularidad(0);
        return packageRepository.save(pkg);
    }

    public Package updatePackage(@NonNull Package pkg) {
        return packageRepository.save(pkg);
    }

    public void deletePackage(@NonNull Long id) {
        packageRepository.deleteById(id);
    }

    public boolean existsById(@NonNull Long id) {
        return packageRepository.existsById(id);
    }

    public List<Package> getPackagesByCategory(String categoria) {
        return packageRepository.findByCategoria(categoria);
    }
    
    public List<Package> getPopularPackages() {
        return packageRepository.findByActivoTrueOrderByPopularidadDesc();
    }
}