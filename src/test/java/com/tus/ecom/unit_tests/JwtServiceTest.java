package com.tus.ecom.unit_tests;

import com.tus.ecom.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtServiceTest {

    private JwtService jwtService;

    private final long expiration = 60000; // 1 minute

    @BeforeEach
    void setUp() throws Exception {

        jwtService = new JwtService();

        String secret = "mysecretkeymysecretkeymysecretkey123456";
        setField(jwtService, "secret", secret);
        setField(jwtService, "expiration", expiration);
    }

    private void setField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @Test
    void generateTokenTest() {

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("Joe");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtService.extractUsername(token).equals("Joe"));
    }

    @Test
    void extractUsernameTest() {

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("Sam");

        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("Sam", username);
    }

    @Test
    void isTokenValidTestTrue() {

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("Joe");

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValidTestFalseExpired() throws Exception {

        // Set very short expiration to simulate expiry
        setField(jwtService, "expiration", 1L);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("Joe");

        String token = jwtService.generateToken(userDetails);

        // Wait to ensure token expires
        Thread.sleep(10);

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void createJwtCookieTest() {

        String token = "sampleToken";

        Cookie cookie = jwtService.createJwtCookie(token);

        assertEquals("jwt", cookie.getName());
        assertEquals(token, cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertTrue(cookie.isHttpOnly());
        assertEquals((int) (expiration / 1000), cookie.getMaxAge());
    }

    @Test
    void clearJwtCookieTest() {

        Cookie cookie = jwtService.clearJwtCookie();

        assertEquals("jwt", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getMaxAge());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
    }
}