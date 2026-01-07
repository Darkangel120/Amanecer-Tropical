package com.AmanecerTropical.service;

import com.AmanecerTropical.entity.User;
import com.AmanecerTropical.repository.UserRepository;
import io.micrometer.common.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
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

    @SuppressWarnings("null")
    public Optional<User> getUserById(@NonNull Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(@NonNull String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(@NonNull User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @SuppressWarnings("null")
    public User updateUser(@NonNull User user) {
        // Fetch existing user to preserve password and other fields not sent in update
        Optional<User> existingUserOpt = userRepository.findById(user.getId());
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            // Preserve password
            user.setPassword(existingUser.getPassword());
            // Preserve role if not set
            if (user.getRole() == null) {
                user.setRole(existingUser.getRole());
            }
            // Handle profile picture
            if (user.getProfilePicture() != null && user.getProfilePicture().startsWith("data:image")) {
                // New base64 image provided, save as file
                try {
                    String filePath = saveProfilePicture(user.getProfilePicture(), user.getId());
                    user.setProfilePicture(filePath);
                } catch (IOException e) {
                    System.err.println("Error saving profile picture: " + e.getMessage());
                    // Keep existing picture if save fails
                    user.setProfilePicture(existingUser.getProfilePicture());
                }
            } else if (user.getProfilePicture() == null) {
                // Preserve existing profile picture
                user.setProfilePicture(existingUser.getProfilePicture());
            }
            // If profilePicture is already a file path, keep it as is
            System.out.println("Updating user: " + user.getId() + ", profilePicture: " + user.getProfilePicture());
        }
        try {
            User savedUser = userRepository.save(user);
            System.out.println("User saved with profilePicture: " + savedUser.getProfilePicture());
            return savedUser;
        } catch (Exception e) {
            System.err.println("Error saving user: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("null")
    public void deleteUser(@NonNull Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByEmail(@NonNull String email) {
        return userRepository.existsByEmail(email);
    }

    public List<User> getUsersByRole(@NonNull User.UserRole role) {
        return userRepository.findByRole(role);
    }

    private String saveProfilePicture(String base64Image, Long userId) throws IOException {
        // Create uploads directory if it doesn't exist
        Path uploadDir = Paths.get("src/main/resources/static/uploads/profile-pictures");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Extract image data from base64 string
        String[] parts = base64Image.split(",");
        String imageData = parts[1];
        String contentType = parts[0].split(":")[1].split(";")[0];
        String extension = contentType.split("/")[1]; // e.g., "jpeg", "png"

        // Generate unique filename
        String fileName = "profile_" + userId + "_" + UUID.randomUUID().toString() + "." + extension;
        Path filePath = uploadDir.resolve(fileName);

        // Decode and save the image
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        Files.write(filePath, imageBytes);

        // Return the relative path for database storage
        return "/uploads/profile-pictures/" + fileName;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el Email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
