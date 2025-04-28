package coptic.user_api.services;

import coptic.user_api.models.User;
import coptic.user_api.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    //Mock dependencies
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    //Inject into service
    @InjectMocks
    private UserService userService;

    private User user;

    //Setup a user object before each test
    @BeforeEach
    public void setUp() {
        user = new User("test@example.com", "plaintextPassword");
    }

    //Test createUser (hash password & save)
    @Test
    public void testCreateUser_ShouldEncodePasswordAndSaveUser() {
        when(passwordEncoder.encode("plaintextPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.createUser(user);

        assertEquals("hashedPassword", savedUser.getPassword());
        verify(userRepository).save(savedUser);
    }

    //Test findUserByEmail
    @Test
    public void testFindUserByEmail_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        User result = userService.findUserByEmail("test@example.com");

        assertEquals(user, result);
    }

    //Test emailExists
    @Test
    public void testEmailExists_ShouldReturnTrueIfExists() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean exists = userService.emailExists("test@example.com");

        assertTrue(exists);
    }

    //Test checkPassword (match)
    @Test
    public void testCheckPassword_ShouldReturnTrueWhenMatch() {
        when(passwordEncoder.matches("raw", "hashed")).thenReturn(true);

        boolean match = userService.checkPassword("raw", "hashed");

        assertTrue(match);
    }

    //Test checkPassword (no match)
    @Test
    public void testCheckPassword_ShouldReturnFalseWhenNotMatch() {
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        boolean match = userService.checkPassword("wrong", "hashed");

        assertFalse(match);
    }

    //Test encodePassword
    @Test
    public void testEncodePassword_ShouldReturnEncodedPassword() {
        when(passwordEncoder.encode("123")).thenReturn("encoded123");

        String encoded = userService.encodePassword("123");

        assertEquals("encoded123", encoded);
    }
}
