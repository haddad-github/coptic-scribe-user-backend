package coptic.user_api.services;

import coptic.user_api.models.Bookmark;
import coptic.user_api.models.User;

import coptic.user_api.repositories.BookmarkRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //Enable Mockito annotations
public class BookmarkServiceTest {

    //Mock repository
    @Mock
    private BookmarkRepository bookmarkRepository;

    //Inject into service
    @InjectMocks
    private BookmarkService bookmarkService;

    //Sample user and bookmark
    private User user;
    private Bookmark bookmark;

    //Initialize user and bookmark before each test
    @BeforeEach
    public void setUp() {
        user = new User("test@example.com", "pass");
        bookmark = new Bookmark("Sample", user, "ⲡⲉⲧⲟⲩ", "petou", "the one", "الواحد", "notes");
    }

    //Test saveBookmark
    //Should call repository.save() and return the saved bookmark
    @Test
    public void testSaveBookmark_ShouldCallRepositoryAndReturnSaved() {
        when(bookmarkRepository.save(bookmark)).thenReturn(bookmark);

        Bookmark result = bookmarkService.saveBookmark(bookmark);

        assertEquals(bookmark, result);
        verify(bookmarkRepository).save(bookmark);
    }

    //Test getBookmarksByUser
    //Should return the list of bookmarks for a given user
    @Test
    public void testGetBookmarksByUser_ShouldReturnUserBookmarks() {
        List<Bookmark> bookmarks = Arrays.asList(bookmark);
        when(bookmarkRepository.findByUser(user)).thenReturn(bookmarks);

        List<Bookmark> result = bookmarkService.getBookmarksByUser(user);

        assertEquals(bookmarks, result);
    }

    //Test getBookmarkById
    //Should return the bookmark wrapped in Optional if found
    @Test
    public void testGetBookmarkById_ShouldReturnBookmarkIfExists() {
        when(bookmarkRepository.findById(1)).thenReturn(Optional.of(bookmark));

        Optional<Bookmark> result = bookmarkService.getBookmarkById(1);

        assertTrue(result.isPresent());
        assertEquals(bookmark, result.get());
    }

    //Test deleteBookmarkById
    //Should call repository.deleteById()
    @Test
    public void testDeleteBookmarkById_ShouldCallRepositoryDelete() {
        bookmarkService.deleteBookmarkById(1);

        verify(bookmarkRepository).deleteById(1);
    }

    //Test updateBookmark
    //Should call save() and flush() and return the updated bookmark
    @Test
    public void testUpdateBookmark_ShouldSaveAndFlush() {
        when(bookmarkRepository.save(bookmark)).thenReturn(bookmark);

        Bookmark result = bookmarkService.updateBookmark(bookmark);

        verify(bookmarkRepository).save(bookmark);
        verify(bookmarkRepository).flush();
        assertEquals(bookmark, result);
    }
}
