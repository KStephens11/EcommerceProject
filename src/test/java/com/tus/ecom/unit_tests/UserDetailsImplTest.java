package com.tus.ecom.unit_tests;

import com.tus.ecom.model.RoleEntity;
import com.tus.ecom.model.UserEntity;
import com.tus.ecom.service.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDetailsImplTest {

    @Test
    void getUsernameAndPasswordTest() {
        UserEntity user = new UserEntity(1, "joe", "pass123", new RoleEntity(1L, "ADMIN"));

        UserDetailsImpl details = new UserDetailsImpl(user);

        assertEquals("joe", details.getUsername());
        assertEquals("pass123", details.getPassword());
    }

    @Test
    void getAuthoritiesTest() {
        UserEntity user = new UserEntity(1, "joe", "pass123", new RoleEntity(1L, "ADMIN"));

        UserDetailsImpl details = new UserDetailsImpl(user);

        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN")));
    }

    @Test
    void getAuthoritiesRoleEntityNullTest() {
        UserEntity user = new UserEntity(1, "joe", "pass123", null);

        UserDetailsImpl details = new UserDetailsImpl(user);

        IllegalStateException ex = assertThrows(IllegalStateException.class, details::getAuthorities);
        assertEquals("User role is not set for username: joe", ex.getMessage());
    }

    @Test
    void getAuthoritiesRoleNameNullTest() {
        RoleEntity role = new RoleEntity(1L, null);
        UserEntity user = new UserEntity(1, "joe", "pass123", role);

        UserDetailsImpl details = new UserDetailsImpl(user);

        IllegalStateException ex = assertThrows(IllegalStateException.class, details::getAuthorities);
        assertEquals("User role is not set for username: joe", ex.getMessage());
    }
}