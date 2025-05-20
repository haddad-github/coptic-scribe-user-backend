//Package where this class belongs
package coptic.user_api.controllers;

//Import Spring Boot dependencies
import org.springframework.web.bind.annotation.*;

//Import HTTP response
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

//Import User model & service
import coptic.user_api.models.User;
import coptic.user_api.services.UserService;
import coptic.user_api.services.PasswordResetService;

//Import JWT class
import coptic.user_api.security.JWT;

import java.util.Collections;
import java.util.Map;

//@RestController marks this as a controller that handles API requests
//@RequestMapping all routes in this controller start with /users
@RestController
@RequestMapping("/users")
public class UserController {
    //JWT
    private final JWT jwt;

    //Inject UserService dependency
    private final UserService userService;

    //Inject PasswordResetService (handles generation/verification of reset tokens)
    private final PasswordResetService passwordResetService;

    //Constructor, pass through UserService as "userService"
    //Pass through the JWT for authentication
    public UserController(UserService userService, JWT jwt, PasswordResetService passwordResetService){
        this.userService = userService;
        this.jwt = jwt;
        this.passwordResetService = passwordResetService;
    }

    /**
     * User sign-up (creates a new user)
     * @param user The user object received from the request
     * @return ResponseEntity with status message
     */
    //@PostMapping [POST] route
    @PostMapping("/sign_up")
    public ResponseEntity<?> signUp(@RequestBody User user) {

        //Check if user's email exists
        boolean exists = userService.emailExists(user.getEmail());

        //If it does, raise conflict that the email already exist
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }

        //Otherwise, create the user
        User createdUser = userService.createUser(user);

        //For auto-login after sign-up, generate JWT token
        String token = jwt.generateToken(createdUser.getEmail());

        //Return response with JWT
        return ResponseEntity.status(HttpStatus.CREATED).body(Collections.singletonMap("token", token));
    }

    /**
     * User login (check if user exists)
     * @param user The user object with email & password
     * @return ResponseEntity with status message
     */
    //@PostMapping [POST] route
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user){

        //Find user by email
        User foundUser = userService.findUserByEmail(user.getEmail());

        //If no user found or password does not match, return unauthorized response
        if (foundUser == null || !userService.checkPassword(user.getPassword(), foundUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials!");
        }

        //Generate JWT token
        String token = jwt.generateToken(user.getEmail());

        //Otherwise return successful response with token
        return ResponseEntity.ok().body(Collections.singletonMap("token", token));
    }

    /**
     * Change password for a logged-in user
     * @param payload Contains email, oldPassword, newPassword
     * @return ResponseEntity with status message
     */
    @PutMapping("/change_password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        //Check if user exists
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        //Verify current password
        if (!userService.checkPassword(oldPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password");
        }

        //Update password
        user.setPassword(userService.encodePassword(newPassword));
        userService.createUser(user); // Save updated user

        return ResponseEntity.ok("Password changed successfully");
    }

    /**
     * Request a password reset link
     * @param payload Contains the email of the user
     * @return The generated reset link (for now)
     */
    @PostMapping("/request_reset")
    public ResponseEntity<?> requestReset(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        String link = passwordResetService.generateResetToken(email);

        if (link == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        //Placeholder
        return ResponseEntity.ok(Collections.singletonMap("reset_link", link));
    }

    /**
     * Handle reset using a token and new password
     * @param payload Contains token and newPassword
     * @return success or failure
     */
    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        String newPassword = payload.get("newPassword");

        boolean success = passwordResetService.resetPassword(token, newPassword);

        if (!success) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
        }

        return ResponseEntity.ok("Password has been reset successfully");
    }
}
