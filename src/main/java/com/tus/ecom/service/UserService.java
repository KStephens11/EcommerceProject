package com.tus.ecom.service;

import java.util.*;

import com.tus.ecom.dto.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tus.ecom.dto.UserRequest;
import com.tus.ecom.model.RoleEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.RoleRepository;
import com.tus.ecom.repository.UserRepository;

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

        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new UsernameNotFoundException("Username is required");
        }

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        verifyPassword(req);

        Optional<RoleEntity> roleEntity = roleRepository.findByName(req.getRole());
        if (roleEntity.isEmpty()) {
            throw new IllegalArgumentException("Invalid role: " + req.getRole());
        }

        UserEntity user = new UserEntity();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(roleEntity.get());

        UserEntity userEntity = userRepository.save(user);

        return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getRole());

    }

    private void verifyPassword(UserRequest req) {
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (req.getPassword().length() < MIN_PASSWORD_LENGTH) throw new IllegalArgumentException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");

        if (checkPassword(req.getPassword())) throw new IllegalArgumentException("Password must contain at least one uppercase letter and one digit.");
    }

    public UserResponse updateUser(UserRequest req) {

        if (req.getUsername() == null || req.getUsername().isBlank()) {
            throw new UsernameNotFoundException("Username is required");
        }

        Optional<UserEntity> user = userRepository.getUserEntityById(req.getId());
        if (user.isEmpty()) {
            throw new IllegalArgumentException("Invalid id: " + req.getId());
        }

        Optional<UserEntity> existing = userRepository.findByUsername(req.getUsername());
        if (existing.isPresent() && !Objects.equals(existing.get().getId(), req.getId())) {
            throw new IllegalArgumentException("Username already exists");
        }

        verifyPassword(req);

        Optional<RoleEntity> roleEntity = roleRepository.findByName(req.getRole());
        if (roleEntity.isEmpty()) {
            throw new IllegalArgumentException("Invalid role: " + req.getRole());
        }

        user.get().setUsername(req.getUsername());
        user.get().setPassword(passwordEncoder.encode(req.getPassword()));
        user.get().setRole(roleEntity.get());

        UserEntity userEntity = userRepository.save(user.get());

        return new UserResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getRole());

    }

    public void deleteUser(Integer id) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth != null ? auth.getName() : null;

        if (currentUsername != null) {
            userRepository.findByUsername(currentUsername).ifPresent(currentUser -> {
                if (Objects.equals(currentUser.getId(), id)) {
                    throw new IllegalArgumentException("You cannot delete your own account.");
                }
                else {
                    userRepository.deleteById(id);
                }
            });
        }
        else {
            throw new IllegalArgumentException("You must be logged in to delete a user.");
        }

    }

    public boolean checkPassword(String password) {

        boolean hasUpper = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }

            if (hasUpper && hasDigit) {
                return false;
            }
        }
        return true;
    }

    public Optional<UserResponse> getUsersById(Integer id) {
        return userRepository.findById(id).map(user -> new UserResponse(user.getId(), user.getUsername(), user.getRole()));
    }

    public List<UserResponse> getUsernames() {
        return userRepository.findAll().stream().map(user -> new UserResponse(user.getId(), user.getUsername(), user.getRole())).toList();
    }

}
