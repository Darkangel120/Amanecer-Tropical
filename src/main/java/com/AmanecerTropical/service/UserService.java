package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.User;
import com.AmanecerTropical.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(@NonNull Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(@NonNull String email) {
        return userRepository.findByCorreoElectronico(email);
    }

    public boolean existsByCedula(@NonNull String cedula) {
        return userRepository.existsByCedula(cedula);
    }

    public User createUser(@NonNull User user) {
        user.setContrasena(passwordEncoder.encode(user.getContrasena()));
        return userRepository.save(user);
    }

    public User updateUser(@NonNull User user) {
        @SuppressWarnings("null")
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (user.getContrasena() == null || user.getContrasena().isEmpty()) {
                user.setContrasena(existingUser.getContrasena());
            } else {
                user.setContrasena(passwordEncoder.encode(user.getContrasena()));
            }
            if (user.getRol() == null) {
                user.setRol(existingUser.getRol());
            }
            if (user.getFotoPerfil() != null && user.getFotoPerfil().startsWith("data:image")) {
                try {
                    String filePath = saveProfilePicture(user.getFotoPerfil(), user.getId());
                    user.setFotoPerfil(filePath);
                } catch (IOException e) {
                    System.err.println("Error saving profile picture: " + e.getMessage());
                    user.setFotoPerfil(existingUser.getFotoPerfil());
                }
            } else if (user.getFotoPerfil() == null) {
                user.setFotoPerfil(existingUser.getFotoPerfil());
            }
        }
        return userRepository.save(user);
    }

    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(@NonNull String email) {
        return userRepository.existsByCorreoElectronico(email);
    }

    public List<User> getUsersByRole(@NonNull String rol) {
        return userRepository.findByRol(rol);
    }

    private String saveProfilePicture(String base64Image, Long userId) throws IOException {
        Path uploadDir = Paths.get("src/main/resources/static/uploads/profile-pictures").toAbsolutePath();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String[] parts = base64Image.split(",");
        String imageData = parts[1];
        String contentType = parts[0].split(":")[1].split(";")[0];
        String extension = contentType.split("/")[1];

        String fileName = "profile_" + userId + "_" + UUID.randomUUID().toString() + "." + extension;
        Path filePath = uploadDir.resolve(fileName);

        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        Files.write(filePath, imageBytes);

        return "/uploads/profile-pictures/" + fileName;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByCorreoElectronico(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el Email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getCorreoElectronico())
                .password(user.getContrasena())
                .roles(user.getRol())
                .build();
    }
}