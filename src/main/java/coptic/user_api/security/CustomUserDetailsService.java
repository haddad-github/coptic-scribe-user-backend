package coptic.user_api.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import coptic.user_api.repositories.UserRepository;
import coptic.user_api.models.User;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructor to inject UserRepository for user lookup
     * @param userRepository The UserRepository used to retrieve users by email
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(username));
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user.get();
    }
}
