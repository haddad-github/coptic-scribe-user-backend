//Package where this class belongs
package coptic.user_api.services;

//Import the Bookmark model AND User model (since there's foreign keys)
import coptic.user_api.models.Bookmark;
import coptic.user_api.models.User;

//Import Bookmark repository
import coptic.user_api.repositories.BookmarkRepository;

//Automated dependency injection
import org.springframework.beans.factory.annotation.Autowired;

//Import to state that it's a Service
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//List utilities
import java.util.List;
import java.util.Optional;

@Service
public class BookmarkService {

    //Inject BookmarkRepository, for database operations
    private final BookmarkRepository bookmarkRepository;

    //Constructor
    //Pass BookmarkRepository into BookmarkService; allowing to use bookmarkRepository without creating it (new BookmarkRepository())
    @Autowired
    public BookmarkService(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    /**
     * Saves a new bookmark; inserts new bookmark into database
     * @param bookmark The bookmark object to save
     * @return The saved Bookmark object
     */
    public Bookmark saveBookmark(Bookmark bookmark) {
        return bookmarkRepository.save(bookmark);
    }

    /**
     * Finds all bookmarks for a given user
     * @param user The User object whose bookmarks are being retrieved
     * @return A list of Bookmark objects that belong to the user
     */
    public List<Bookmark> getBookmarksByUser(User user) {
        return bookmarkRepository.findByUser(user);
    }

    /**
     * Retrieves a bookmark by its ID
     * @param id The ID of the bookmark
     * @return An Optional containing the Bookmark if found, otherwise empty
     */
    public Optional<Bookmark> getBookmarkById(int id) {
        return bookmarkRepository.findById(id);
    }

    /**
     * Deletes a bookmark by its ID
     * @param id The ID of the bookmark to delete
     */
    public void deleteBookmarkById(int id) {
        bookmarkRepository.deleteById(id);
    }

    @Transactional
    public Bookmark updateBookmark(Bookmark updatedBookmark) {
        Bookmark saved = bookmarkRepository.save(updatedBookmark);
        bookmarkRepository.flush(); //force update to DB now
        return saved;
    }
}
