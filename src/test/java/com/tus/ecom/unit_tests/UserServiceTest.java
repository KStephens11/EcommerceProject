package com.tus.ecom.unit_tests;

import com.tus.ecom.dto.user.UserRequest;
import com.tus.ecom.dto.user.UserResponse;
import com.tus.ecom.model.RoleEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.RoleRepository;
import com.tus.ecom.repository.UserRepository;
import com.tus.ecom.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserService userService;
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);

        userService = new UserService(userRepository, roleRepository, passwordEncoder);

        SecurityContextHolder.clearContext();
    }

    @Test
    void createUserTestValid() {

        UserRequest userRequest = new UserRequest(1, "Joe", "Password123");

        when(userRepository.findByUsername("Joe")).thenReturn(Optional.empty());
        when(roleRepository.findByName("CUSTOMER"))
                .thenReturn(Optional.of(new RoleEntity(2L, "CUSTOMER")));
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-Password123");

        when(userRepository.save(any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.createUser(userRequest);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        UserEntity saved = captor.getValue();

        assertEquals("Joe", saved.getUsername());
        assertEquals("encoded-Password123", saved.getPassword());
        assertEquals(2L, saved.getRole().getId());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createUserTestNullAndBlankUsername(String username) {

        UserRequest request = new UserRequest(1, username, "Password123");

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request),
                "Username is required");

        verify(userRepository, never()).save(any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createUserTestNullAndBlankPassword(String password) {

        UserRequest request = new UserRequest(1, "Joe", password);

        when(userRepository.findByUsername("Joe")).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Password is required", e.getMessage());

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void createUserTestInvalidPasswordLength() {

        UserRequest request = new UserRequest(1, "Joe", "123");

        when(userRepository.findByUsername("Joe")).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Password must be at least 8 characters long.", e.getMessage());

        verify(passwordEncoder, never()).encode(any());
    }

    @ParameterizedTest
    @CsvSource({"password1", "Password"})
    void createUserTestInvalidPassword(String password) {

        UserRequest request = new UserRequest(1, "Joe", password);

        when(userRepository.findByUsername("Joe")).thenReturn(Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Password must contain at least one uppercase letter and one digit.",
                e.getMessage());

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void createUserTestDuplicateUsername() {

        when(userRepository.findByUsername("Joe"))
                .thenReturn(Optional.of(new UserEntity()));

        UserRequest request = new UserRequest(1, "Joe", "Password123");

        Exception e = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(request));

        assertEquals("Username already exists", e.getMessage());
    }

    @Test
    void getUsersByIdTest() {

        UserEntity user = new UserEntity(
                1,
                "Joe",
                "Password123",
                new RoleEntity(1L, "ADMIN")
        );

        when(userRepository.findById(1))
                .thenReturn(Optional.of(user));

        Optional<UserResponse> response = userService.getUsersById(1);

        assertTrue(response.isPresent());
        assertEquals("Joe", response.get().getUsername());
        assertEquals("ADMIN", response.get().getRole().getName());
    }

    @Test
    void getUsernames() {

        UserEntity user1 = new UserEntity(
                1,
                "Joe",
                "Password123",
                new RoleEntity(1L, "ADMIN")
        );

        UserEntity user2 = new UserEntity(
                2,
                "Sam",
                "Password456",
                new RoleEntity(2L, "CUSTOMER_SERVICE")
        );

        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserResponse> responses = userService.getUsernames();

        assertEquals(2, responses.size());
        assertEquals("Joe", responses.get(0).getUsername());
        assertEquals("Sam", responses.get(1).getUsername());
    }
}