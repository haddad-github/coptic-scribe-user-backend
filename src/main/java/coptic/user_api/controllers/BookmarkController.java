//Package where this class belongs
package coptic.user_api.controllers;

//Import Spring Boot dependencies
import org.springframework.web.bind.annotation.*;

//Import HTTP response
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

//Import Bookmark model & service
import coptic.user_api.models.Bookmark;
import coptic.user_api.models.User;
import coptic.user_api.services.BookmarkService;
import coptic.user_api.services.UserService;

//Import for list manipulation
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

//@RestController marks this as a controller that handles API requests
//@RequestMapping all routes in this controller start with /bookmarks
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    //Inject BookmarkService dependency
    private final BookmarkService bookmarkService;

    //Inject UserService dependency (for foreign key)
    private final UserService userService;

    // Constructor, pass through BookmarkService as "bookmarkService" and UserService as "userService"
    public BookmarkController(BookmarkService bookmarkService, UserService userService) {
        this.bookmarkService = bookmarkService;
        this.userService = userService;
    }

    /**
     * Add a new bookmark for a user
     * @param payload The bookmark object received from the request
     * @return ResponseEntitywith status message
     */
     //@PostMapping [POST] route
    @PostMapping("/add")
    public ResponseEntity<String> addBookmark(@RequestBody Map<String, Object> payload) {
        String email = (String) payload.get("email");
        String name = (String) payload.get("name");
        String copticText = (String) payload.get("copticText");
        String transliteration = (String) payload.get("transliteration");
        String englishTranslation = (String) payload.get("englishTranslation");
        String arabicTranslation = (String) payload.get("arabicTranslation");
        String notes = (String) payload.get("notes");

        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        }

        Bookmark bookmark = new Bookmark(name, user, copticText, transliteration, englishTranslation, arabicTranslation, notes);
        bookmarkService.saveBookmark(bookmark);

        return ResponseEntity.status(HttpStatus.CREATED).body("Bookmark added successfully!");
    }


    /**
     * Get all bookmarks for a specific user
     * @param email The email of the user whose bookmarks are fetched
     * @return ResponseEntity with the list of bookmarks
     */
     //GetMapping [GET] route
     //@PathVariable extracts a value directly from the URL path (ex: /user/email_here@example.com)
    @GetMapping("/user/{email}")
    public ResponseEntity<List<Bookmark>> getUserBookmarks(@PathVariable String email) {
        //Find user by email
        User user = userService.findUserByEmail(email);

        //If no user, return empty list
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        //Otherwise get list of bookmarks associated to user and return it as a response
        List<Bookmark> bookmarks = bookmarkService.getBookmarksByUser(user);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * Rename or update the content of an existing bookmark
     * @param id The ID of the bookmark to update
     * @param payload The new bookmark fields (name, text, translations, notes)
     * @return ResponseEntity with status message
     */
    @PutMapping("/rename/{id}")
    public ResponseEntity<String> renameBookmark(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        Optional<Bookmark> optionalBookmark = bookmarkService.getBookmarkById(id);
        if (optionalBookmark.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bookmark not found");
        }

        Bookmark bookmark = optionalBookmark.get();

        String newName = (String) payload.get("name");
        String copticText = (String) payload.get("copticText");
        String transliteration = (String) payload.get("transliteration");
        String englishTranslation = (String) payload.get("englishTranslation");
        String arabicTranslation = (String) payload.get("arabicTranslation");
        String notes = (String) payload.get("notes");

        bookmark.setName(newName);
        bookmark.setCopticText(copticText);
        bookmark.setTransliteration(transliteration);
        bookmark.setEnglishTranslation(englishTranslation);
        bookmark.setArabicTranslation(arabicTranslation);
        bookmark.setNotes(notes);

        // ✅ Use updateBookmark (cleaner logic & allows for easier override later if needed)
        bookmarkService.updateBookmark(bookmark);

        return ResponseEntity.ok("Bookmark updated successfully");
    }

    /**
     * Delete a bookmark by its ID
     * @param id The ID of the bookmark to delete
     * @return ResponseEntity with status message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteBookmark(@PathVariable int id) {
        Optional<Bookmark> optional = bookmarkService.getBookmarkById(id);
        if (optional.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bookmark not found");

        bookmarkService.deleteBookmarkById(id);
        return ResponseEntity.ok("Bookmark deleted");
    }


}