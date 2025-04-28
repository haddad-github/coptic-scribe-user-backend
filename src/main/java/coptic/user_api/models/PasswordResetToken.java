package coptic.user_api.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    //Primary key for the password reset token table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Unique token string (used in reset URL)
    @Column(nullable = false, unique = true)
    private String token;

    //User associated with the token (1:1 relationship)
    //Foreign key to users table
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //Date and time when the token expires
    @Column(nullable = false)
    private LocalDateTime expiration;

    /**
     * Constructor to create a new PasswordResetToken object with provided values
     * @param token Unique reset token string
     * @param user User associated with the token
     * @param expiration Expiration date and time of the token
     */
    public PasswordResetToken(String token, User user, LocalDateTime expiration) {
        this.token = token;
        this.user = user;
        this.expiration = expiration;
    }

    //GETTERS & SETTERS
    public int getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getExpiration() { return expiration; }
    public void setExpiration(LocalDateTime expiration) { this.expiration = expiration; }
}
