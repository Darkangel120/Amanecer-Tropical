package com.AmanecerTropical.controller;

import com.AmanecerTropical.entity.Package;
import com.AmanecerTropical.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    @Autowired
    private PackageService packageService;

    @GetMapping
    public ResponseEntity<List<Package>> getAllPackages() {
        List<Package> packages = packageService.getAllPackages();
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Package> getPackageById(@PathVariable @NonNull Long id) {
        Optional<Package> pkg = packageService.getPackageById(id);
        return pkg.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Package>> searchPackages(@RequestParam(required = false) String nombre,
                                                        @RequestParam(required = false) String ubicacion) {
        List<Package> packages;
        if (nombre != null && !nombre.isEmpty()) {
            packages = packageService.getPackagesByNombre(nombre);
        } else if (ubicacion != null && !ubicacion.isEmpty()) {
            packages = packageService.getPackagesByUbicacion(ubicacion);
        } else {
            packages = packageService.getAllPackages();
        }
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<Package>> getPackagesByPriceRange(@RequestParam double minPrice,
                                                                 @RequestParam double maxPrice) {
        List<Package> packages = packageService.getPackagesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Package>> getAvailablePackages() {
        List<Package> packages = packageService.getAvailablePackages();
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/category/{categoria}")
    public ResponseEntity<List<Package>> getPackagesByCategory(@PathVariable String categoria) {
        List<Package> packages = packageService.getPackagesByCategory(categoria);
        return ResponseEntity.ok(packages);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Package>> getPopularPackages() {
        List<Package> packages = packageService.getPopularPackages();
        return ResponseEntity.ok(packages);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Package> createPackage(@RequestBody @NonNull Package pkg) {
        Package createdPackage = packageService.createPackage(pkg);
        return ResponseEntity.ok(createdPackage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Package> updatePackage(@PathVariable @NonNull Long id, @RequestBody @NonNull Package pkg) {
        Optional<Package> existingPackage = packageService.getPackageById(id);
        if (existingPackage.isPresent()) {
            pkg.setId(id);
            Package updatedPackage = packageService.updatePackage(pkg);
            return ResponseEntity.ok(updatedPackage);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePackage(@PathVariable @NonNull Long id) {
        Optional<Package> pkg = packageService.getPackageById(id);
        if (pkg.isPresent()) {
            packageService.deletePackage(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}