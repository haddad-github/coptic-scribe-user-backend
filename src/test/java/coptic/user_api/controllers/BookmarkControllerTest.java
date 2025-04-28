//Package where this test belongs
package coptic.user_api.controllers;

//Spring Boot test framework

import com.fasterxml.jackson.databind.ObjectMapper;
import coptic.user_api.models.Bookmark;
import coptic.user_api.models.User;
import coptic.user_api.services.BookmarkService;
import coptic.user_api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class) // Enable Spring context
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class BookmarkControllerTest {

    //Simulates HTTP requests
    @Autowired
    private MockMvc mockMvc;

    //Mocks for injected services
    @MockBean
    private BookmarkService bookmarkService;

    @MockBean
    private UserService userService;

    //JSON utility
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Sample user and bookmark
    private User user;
    private Bookmark bookmark;

    //Create reusable test objects
    @BeforeEach
    public void setUp() {
        user = new User("user@example.com", "password");
        bookmark = new Bookmark("Title", user, "ⲡⲁⲣⲧ", "part", "part", "جزء", "Some notes");
        bookmark.setId(1);
    }

    //Test /bookmarks/add with valid user
    @Test
    public void testAddBookmark_ValidUser_ShouldCreateBookmark() throws Exception {
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);

        mockMvc.perform(post("/bookmarks/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", "user@example.com",
                                "name", "Title",
                                "copticText", "ⲡⲁⲣⲧ",
                                "transliteration", "part",
                                "englishTranslation", "part",
                                "arabicTranslation", "جزء",
                                "notes", "Some notes"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(content().string("Bookmark added successfully!"));
    }

    //Test /bookmarks/add with unknown user
    @Test
    public void testAddBookmark_UserNotFound_ShouldReturnBadRequest() throws Exception {
        when(userService.findUserByEmail("ghost@example.com")).thenReturn(null);

        mockMvc.perform(post("/bookmarks/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "ghost@example.com"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User not found"));
    }

    //Test /bookmarks/user/{email}
    @Test
    public void testGetUserBookmarks_ValidUser_ShouldReturnList() throws Exception {
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(bookmarkService.getBookmarksByUser(user)).thenReturn(List.of(bookmark));

        mockMvc.perform(get("/bookmarks/user/user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Title"));
    }

    //Test /bookmarks/user/{email} for user not found
    @Test
    public void testGetUserBookmarks_UserNotFound_ShouldReturnEmptyList() throws Exception {
        when(userService.findUserByEmail("ghost@example.com")).thenReturn(null);

        mockMvc.perform(get("/bookmarks/user/ghost@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("[]"));
    }

    //Test /bookmarks/rename/{id}
    @Test
    public void testRenameBookmark_ShouldUpdateAndReturnOk() throws Exception {
        when(bookmarkService.getBookmarkById(1)).thenReturn(Optional.of(bookmark));

        mockMvc.perform(put("/bookmarks/rename/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Updated Title",
                                "copticText", "ⲧⲁⲓ",
                                "transliteration", "tai",
                                "englishTranslation", "this",
                                "arabicTranslation", "هذا",
                                "notes", "updated notes"
                        ))))
                .andExpect(status().isOk())
                .andExpect(content().string("Bookmark updated successfully"));
    }

    //Test /bookmarks/rename/{id} for missing ID
    @Test
    public void testRenameBookmark_NotFound_ShouldReturn404() throws Exception {
        when(bookmarkService.getBookmarkById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/bookmarks/rename/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.emptyMap())))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Bookmark not found"));
    }

    //Test /bookmarks/delete/{id}
    @Test
    public void testDeleteBookmark_ShouldDeleteSuccessfully() throws Exception {
        when(bookmarkService.getBookmarkById(1)).thenReturn(Optional.of(bookmark));

        mockMvc.perform(delete("/bookmarks/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Bookmark deleted"));
    }

    //Test /bookmarks/delete/{id} for unknown ID
    @Test
    public void testDeleteBookmark_NotFound_ShouldReturn404() throws Exception {
        when(bookmarkService.getBookmarkById(42)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/bookmarks/delete/42"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Bookmark not found"));
    }
}
