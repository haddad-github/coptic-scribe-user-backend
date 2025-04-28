//Package where this class belongs
package coptic.user_api.repositories;

//Import model
import coptic.user_api.models.PasswordResetToken;
import coptic.user_api.models.User;

//Import JPA + annotations
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    /**
     * Finds a PasswordResetToken by its token string
     * @param token The token string to search for
     * @return The PasswordResetToken object if found, otherwise null
     */
    PasswordResetToken findByToken(String token);

    /**
     * Deletes all reset tokens associated with a user
     * @param user The user whose tokens should be deleted
     */
    void deleteByUser(User user);
}
