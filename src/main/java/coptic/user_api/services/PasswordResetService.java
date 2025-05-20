//Package
package coptic.user_api.services;

//Imports
import coptic.user_api.models.User;
import coptic.user_api.models.PasswordResetToken;
import coptic.user_api.repositories.PasswordResetTokenRepository;
import coptic.user_api.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    //Repositories
    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepo;

    //Password encoder for secure hashing
    private final PasswordEncoder passwordEncoder;

    //Constructor for dependency injection of PasswordResetTokenRepository, UserRepository, and PasswordEncoder
    @Autowired
    public PasswordResetService(PasswordResetTokenRepository tokenRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.tokenRepo = tokenRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Generate a token and "send" email
     * @param email User's email
     * @return The reset link (for mock purposes)
     */
    public String generateResetToken(String email) {
        User user = userRepo.findByEmail(email);
        if (user == null) return null;

        //Clean old tokens
        tokenRepo.deleteByUser(user);

        //Generate new token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(30);

        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiration);
        tokenRepo.save(resetToken);

        //Placeholder
        return "http://localhost:3000/reset-password?token=" + token;
    }

    /**
     * Validate token and update password
     * @param token Token received
     * @param newPassword New raw password
     * @return true if success, false if invalid or expired
     */
    public boolean resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepo.findByToken(token);

        if (resetToken == null || resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword)); // secure hash

        userRepo.save(user);

        //Cleanup
        tokenRepo.delete(resetToken);

        return true;
    }
}
