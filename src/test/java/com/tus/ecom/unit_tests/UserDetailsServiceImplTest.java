package com.tus.ecom.unit_tests;

import com.tus.ecom.model.RoleEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.repository.UserRepository;
import com.tus.ecom.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsernameTest() {
        UserEntity user = new UserEntity(1, "joe", "pass123", new RoleEntity(1L, "ADMIN"));
        when(userRepository.findByUsername("joe")).thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("joe");

        assertEquals("joe", details.getUsername());
        assertEquals("pass123", details.getPassword());
        assertTrue(details.getAuthorities().stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsernameNotFoundTest() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing")
        );

        assertEquals("User not found", ex.getMessage());
    }
}