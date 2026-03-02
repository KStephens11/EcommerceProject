package com.tus.ecom.service;

import com.tus.ecom.dto.user.UserRequest;
import com.tus.ecom.dto.user.UserResponse;
import com.tus.ecom.model.RoleEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.RoleRepository;
import com.tus.ecom.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest req) {

        String username = req.getUsername() == null ? null : req.getUsername().trim();

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (req.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long."
            );
        }

        if (checkPassword(req.getPassword())) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter and one digit."
            );
        }

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Optional<RoleEntity> roleEntity = roleRepository.findByName("CUSTOMER");

        if (roleEntity.isEmpty()) {
            throw new IllegalArgumentException("Customer role not found");
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(roleEntity.get());

        UserEntity saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getRole()
        );
    }

    public boolean checkPassword(String password) {

        boolean hasUpper = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {

            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;

            if (hasUpper && hasDigit) return false;
        }

        return true;
    }

    public Optional<UserResponse> getUsersById(Integer id) {
        return userRepository.findById(id)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole()
                ));
    }

    public List<UserResponse> getUsernames() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole()
                ))
                .toList();
    }
}