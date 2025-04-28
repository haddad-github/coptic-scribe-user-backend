//Package where this class belongs
package coptic.user_api.repositories;

//Import the User model
import coptic.user_api.models.User;

//Import JpaRepository, for built-in CRUD
import org.springframework.data.jpa.repository.JpaRepository;

//Import to state that it's a repository
import org.springframework.stereotype.Repository;

//<Entity, Primary Key type>
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Finds a user by email
     * @param email The email to search for
     * @return The User object if found, otherwise null
     */
    User findByEmail(String email);

    /**
     * Checks if a user exists by email
     * @param email The email to check
     * @return true if a user exists with this email, false otherwise
     */
    boolean existsByEmail(String email);
}
