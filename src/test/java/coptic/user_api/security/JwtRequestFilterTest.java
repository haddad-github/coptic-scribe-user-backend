// Package where this test class belongs
package coptic.user_api.security;

// Import core Spring testing + mocking tools

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JwtRequestFilterTest {

    // Filter instance to test
    private JwtRequestFilter jwtRequestFilter;

    // Dependencies to mock
    private JWT jwt;
    private UserDetailsService userDetailsService;

    // Mock HTTP context
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    /**
     * Prepare mock dependencies and filter before each test
     */
    @BeforeEach
    public void setUp() throws Exception {
        jwt = mock(JWT.class);
        userDetailsService = mock(UserDetailsService.class);
        jwtRequestFilter = new JwtRequestFilter();

        //Use reflection to inject mocks into private fields
        Field jwtField = JwtRequestFilter.class.getDeclaredField("jwt");
        jwtField.setAccessible(true);
        jwtField.set(jwtRequestFilter, jwt);

        Field udsField = JwtRequestFilter.class.getDeclaredField("userDetailsService");
        udsField.setAccessible(true);
        udsField.set(jwtRequestFilter, userDetailsService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    /**
     * Should extract token from header, validate it, load user, and set authentication
     */
    @Test
    public void testValidToken_SetsAuthentication() throws ServletException, IOException {
        //Arrange
        String token = "mocked.jwt.token";
        String email = "user@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwt.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwt.validateToken(token, email)).thenReturn(true);

        //Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        //Assert: the authentication should be set
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());

        //Ensure filter chain continues
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Should skip authentication if header is missing or malformed
     */
    @Test
    public void testNoAuthorizationHeader_SkipsAuthentication() throws ServletException, IOException {
        //Arrange: no token
        when(request.getHeader("Authorization")).thenReturn(null);

        //Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        //Assert: no authentication set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    /**
     * Should skip authentication if token is invalid
     */
    @Test
    public void testInvalidToken_SkipsAuthentication() throws ServletException, IOException {
        //Arrange
        String token = "invalid.jwt";
        String email = "user@example.com";
        UserDetails userDetails = new User(email, "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwt.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails); // ← this was missing
        when(jwt.validateToken(token, email)).thenReturn(false);

        //Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        //Assert: still no authentication
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}