package coptic.user_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import coptic.user_api.models.User;
import coptic.user_api.security.JWT;
import coptic.user_api.services.PasswordResetService;
import coptic.user_api.services.UserService;
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

import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class) //Enable Spring context
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class UserControllerTest {

    //Inject MockMvc to simulate HTTP calls
    @Autowired
    private MockMvc mockMvc;

    //Mock service dependencies
    @MockBean
    private UserService userService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private JWT jwt;

    //ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper();

    //Test /users/sign_up (email already exists)
    @Test
    public void testSignUp_EmailExists_ShouldReturnConflict() throws Exception {
        User user = new User("exists@example.com", "pass");

        when(userService.emailExists(user.getEmail())).thenReturn(true);

        mockMvc.perform(post("/users/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Email already exists!"));
    }

    //Test /users/sign_up (successful)
    @Test
    public void testSignUp_NewUser_ShouldReturnToken() throws Exception {
        User user = new User("new@example.com", "pass");

        when(userService.emailExists(user.getEmail())).thenReturn(false);
        when(userService.createUser(any())).thenReturn(user);
        when(jwt.generateToken(user.getEmail())).thenReturn("mockToken");

        mockMvc.perform(post("/users/sign_up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mockToken"));
    }

    //Test /users/login (valid credentials)
    @Test
    public void testLogin_ValidCredentials_ShouldReturnToken() throws Exception {
        User user = new User("login@example.com", "pass");

        when(userService.findUserByEmail(user.getEmail())).thenReturn(user);
        when(userService.checkPassword("pass", "pass")).thenReturn(true);
        when(jwt.generateToken(user.getEmail())).thenReturn("loginToken");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("loginToken"));
    }

    //Test /users/login (invalid credentials)
    @Test
    public void testLogin_InvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        User user = new User("wrong@example.com", "wrong");

        when(userService.findUserByEmail(user.getEmail())).thenReturn(null);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials!"));
    }

    //Test /users/request_reset (user found)
    @Test
    public void testRequestReset_UserFound_ShouldReturnResetLink() throws Exception {
        when(passwordResetService.generateResetToken("user@example.com")).thenReturn("http://mocked/reset");

        mockMvc.perform(post("/users/request_reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "user@example.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reset_link").value("http://mocked/reset"));
    }

    //Test /users/request_reset (user not found)
    @Test
    public void testRequestReset_UserNotFound_ShouldReturn404() throws Exception {
        when(passwordResetService.generateResetToken("ghost@example.com")).thenReturn(null);

        mockMvc.perform(post("/users/request_reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "ghost@example.com"))))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    //Test /users/reset_password (success)
    @Test
    public void testResetPassword_ValidToken_ShouldReturnSuccess() throws Exception {
        when(passwordResetService.resetPassword("token123", "newPass")).thenReturn(true);

        mockMvc.perform(post("/users/reset_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("token", "token123", "newPassword", "newPass"))))
                .andExpect(status().isOk())
                .andExpect(content().string("Password has been reset successfully"));
    }

    //Test /users/reset_password (invalid or expired)
    @Test
    public void testResetPassword_InvalidToken_ShouldReturnBadRequest() throws Exception {
        when(passwordResetService.resetPassword("invalid", "pass")).thenReturn(false);

        mockMvc.perform(post("/users/reset_password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("token", "invalid", "newPassword", "pass"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired token"));
    }
}
