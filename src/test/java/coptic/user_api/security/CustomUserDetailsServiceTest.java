package coptic.user_api.security;

import coptic.user_api.models.User;
import coptic.user_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {

    //Mock the UserRepository
    @Mock
    private UserRepository userRepository;

    //Inject the mock into the service being tested
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Set up mock environment before each test
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test that loadUserByUsername returns a valid UserDetails
     * when the user is found in the repository.
     */
    @Test
    public void testLoadUserByUsername_UserFound_ShouldReturnUserDetails() {
        //Arrange: create a mock user
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password123");

        //Stub repository to return the mock user
        when(userRepository.findByEmail("test@example.com")).thenReturn(mockUser);

        //Act: call the method
        UserDetails result = customUserDetailsService.loadUserByUsername("test@example.com");

        //Assert: check returned user details
        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
        assertEquals("password123", result.getPassword());
    }

    /**
     * Test that loadUserByUsername throws an exception
     * when the user is not found in the repository.
     */
    @Test
    public void testLoadUserByUsername_UserNotFound_ShouldThrowException() {
        //Stub repository to return null
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        //Act + Assert: expect UsernameNotFoundException
        assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("missing@example.com"));
    }
}
