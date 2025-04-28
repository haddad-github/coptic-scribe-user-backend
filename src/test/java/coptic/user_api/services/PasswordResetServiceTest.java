package coptic.user_api.services;

import coptic.user_api.models.User;
import coptic.user_api.models.PasswordResetToken;

import coptic.user_api.repositories.PasswordResetTokenRepository;
import coptic.user_api.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //Enable Mockito annotations
public class PasswordResetServiceTest {

    //Mock dependencies
    @Mock
    private PasswordResetTokenRepository tokenRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    //Inject service with mocks
    @InjectMocks
    private PasswordResetService passwordResetService;

    //Sample user and token
    private User user;
    private PasswordResetToken token;

    //Setup reusable user and token
    @BeforeEach
    public void setUp() {
        user = new User("reset@example.com", "oldPassword");
        token = new PasswordResetToken("abc123", user, LocalDateTime.now().plusMinutes(30));
    }

    //Test generateResetToken
    //Should delete old tokens, create new token, and return reset link
    @Test
    public void testGenerateResetToken_ShouldReturnResetLink() {
        when(userRepo.findByEmail("reset@example.com")).thenReturn(user);

        String link = passwordResetService.generateResetToken("reset@example.com");

        assertNotNull(link);
        assertTrue(link.contains("http://localhost:3000/reset-password?token="));
        verify(tokenRepo).deleteByUser(user);
        verify(tokenRepo).save(any(PasswordResetToken.class));
    }

    //Test generateResetToken when user does not exist
    //Should return null
    @Test
    public void testGenerateResetToken_UserNotFound_ShouldReturnNull() {
        when(userRepo.findByEmail("nope@example.com")).thenReturn(null);

        String link = passwordResetService.generateResetToken("nope@example.com");

        assertNull(link);
        verify(tokenRepo, never()).save(any());
    }

    //Test resetPassword when token is valid and not expired
    //Should encode password, save user, delete token, and return true
    @Test
    public void testResetPassword_ValidToken_ShouldResetPassword() {
        when(tokenRepo.findByToken("abc123")).thenReturn(token);
        when(passwordEncoder.encode("newPassword")).thenReturn("hashedNew");

        boolean result = passwordResetService.resetPassword("abc123", "newPassword");

        assertTrue(result);
        verify(userRepo).save(any(User.class));
        verify(tokenRepo).delete(token);
    }

    //Test resetPassword when token is null
    //Should return false
    @Test
    public void testResetPassword_InvalidToken_ShouldReturnFalse() {
        when(tokenRepo.findByToken("invalid")).thenReturn(null);

        boolean result = passwordResetService.resetPassword("invalid", "newPassword");

        assertFalse(result);
    }

    //Test resetPassword when token is expired
    //Should return false
    @Test
    public void testResetPassword_ExpiredToken_ShouldReturnFalse() {
        token.setExpiration(LocalDateTime.now().minusMinutes(5));
        when(tokenRepo.findByToken("abc123")).thenReturn(token);

        boolean result = passwordResetService.resetPassword("abc123", "newPassword");

        assertFalse(result);
    }
}
