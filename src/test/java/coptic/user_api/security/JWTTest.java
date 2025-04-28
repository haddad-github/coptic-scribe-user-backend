package coptic.user_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JWTTest {

    //Instance of the JWT utility being tested
    private JWT jwt;

    //Hardcoded test secret key (Base64-encoded)
    private static final String VALID_BASE64_SECRET = "c29tZXZlcnlzZWN1cmVhbmRsb25nYmFzZTY0c2VjcmV0a2V5"; // 44 chars = 33 bytes

    /**
     * Create a new JWT instance using a test secret key
     */
    @BeforeEach
    public void setUp() {
        jwt = new JWT(VALID_BASE64_SECRET);
    }

    /**
     * Test that generateToken creates a valid JWT and extractEmail returns the original email
     */
    @Test
    public void testGenerateTokenAndExtractEmail_ShouldMatchOriginalEmail() {
        //Arrange: email to encode in the token
        String email = "user@example.com";

        //Act: generate token and extract it back
        String token = jwt.generateToken(email);
        String extractedEmail = jwt.extractEmail(token);

        //Assert: make sure it matches
        assertEquals(email, extractedEmail);
    }

    /**
     * Test that validateToken returns true for a freshly generated token
     */
    @Test
    public void testValidateToken_ValidToken_ShouldReturnTrue() {
        //Arrange: generate a new valid token
        String email = "user@example.com";
        String token = jwt.generateToken(email);

        //Act + Assert: should be valid
        assertTrue(jwt.validateToken(token, email));
    }

    /**
     * Test that validateToken returns false if the email does not match the one in the token
     */
    @Test
    public void testValidateToken_WrongEmail_ShouldReturnFalse() {
        //Arrange: token with one email
        String token = jwt.generateToken("user@example.com");

        //Act + Assert: validating with a different email should fail
        assertFalse(jwt.validateToken(token, "other@example.com"));
    }
}
