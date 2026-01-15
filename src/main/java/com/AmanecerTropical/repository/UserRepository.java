package com.AmanecerTropical.repository;

import com.AmanecerTropical.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByCorreoElectronico(String correoElectronico);

    Optional<User> findByCedula(String cedula);

    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByCedula(String cedula);

    List<User> findByRol(String rol);
}