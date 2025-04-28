// Package where this class belongs
package coptic.user_api.repositories;

// Import Bookmark and User models (so we can perform operations on the "bookmarks" table)
import coptic.user_api.models.Bookmark;
import coptic.user_api.models.User;

//Import JpaRepository, for built-in CRUD
import org.springframework.data.jpa.repository.JpaRepository;

//Import Repository annotation to tell Spring this is a repository
import org.springframework.stereotype.Repository;

import java.util.List;

//<Entity, Primary Key type>
@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {

    /**
     * Finds all bookmarks that belong to a specific user
     * @param user The User object whose bookmarks we want to retrieve
     * @return A list of Bookmark objects that belong to the user
     */
    List<Bookmark> findByUser(User user);
}
