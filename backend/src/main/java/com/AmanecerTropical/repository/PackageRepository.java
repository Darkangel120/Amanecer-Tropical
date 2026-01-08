package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    List<Package> findByActivoTrue();

    List<Package> findByCategoria(String categoria);

    List<Package> findByActivoTrueAndCategoria(String categoria);

    @Query("SELECT p FROM Package p WHERE p.activo = true AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.ubicacion) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Package> searchByKeyword(@Param("keyword") String keyword);

    List<Package> findByActivoTrueOrderByNombreAsc();

    List<Package> findByNombreContainingIgnoreCase(String nombre);
    
    List<Package> findByUbicacionContainingIgnoreCase(String ubicacion);
    
    List<Package> findByPrecioBetween(double minPrice, double maxPrice);
    
    List<Package> findByActivoTrueOrderByPopularidadDesc();
}