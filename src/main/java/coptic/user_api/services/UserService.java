//Package where this class belongs
package coptic.user_api.services;

//Import the User model
import coptic.user_api.models.User;

//Import User repository
import coptic.user_api.repositories.UserRepository;

//Automated dependency injection
import org.springframework.beans.factory.annotation.Autowired;

//Import to state that it's a Service
import org.springframework.stereotype.Service;

//Import for password hashing
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    //Inject UserRepository, for database operations
    private final UserRepository userRepository;

    //Inject password encoder (BCrypt)
    private final PasswordEncoder passwordEncoder;

    //Constructor
    //Pass UserRepository into UserService; allowing to use userRepository without creating it (new userRepository())
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create a new user (sign-up)
     * @param user The user object to save
     * @return The saved User object
     */
    public User createUser(User user) {
        //Hash the raw password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Find a user by email (used for login)
     * @param email The email to search for
     * @return The User object if found, otherwise null
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Checks if an email is already registered
     * @param email The email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Check if a raw password matches the user's stored password
     * @param rawPassword The plaintext password input
     * @param storedPassword The hashed password from the database
     * @return true if they match, false otherwise
     */
    public boolean checkPassword(String rawPassword, String storedPassword) {
        return passwordEncoder.matches(rawPassword, storedPassword);
    }

    /**
     * Encodes a raw password using BCrypt
     * @param rawPassword The plaintext password input
     * @return The hashed version of the password
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
