//Package where this class belongs
package coptic.user_api.models;

//JPA annotations for database mapping (@Entity, @Table, @Id, @Column, etc.)
import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

//Entire Class marked as a database entity (table)
//Table name
@Entity
@Table(name = "users")
public class User implements UserDetails {
    //ID = Primary Key
    //GeneratedValue = primary key generated automatically by the database; auto-increment ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Email; cannot be null and must be unique
    @Column(name = "email", nullable = false, unique = true) // This column must not be null & must be unique
    private String email;

    //Password; cannot be null
    @Column(name = "password", nullable = false)
    private String password;

    //Default Constructor required by JPA
    //Used when retrieving data
    public User() {}

    /**
     * Constructor to create a new User object with provided values
     * @param email User's email
     * @param password User's password
     */
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    //GETTERS & SETTERS
    //Id
    public int getId() {return id; }
    public void setId(int id) { this.id = id; }

    //Email
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    //Password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

        @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // No roles for now
    }

    //Using email as username
    @Override
    public String getUsername() {
        return email;
    }

    //Account never expires
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //Account is never locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //Credentials never expire
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //Always enabled
    @Override
    public boolean isEnabled() {
        return true;
    }

}
