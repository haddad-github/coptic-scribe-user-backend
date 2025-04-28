package coptic.user_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class UserApiApplication {

    /**
     * Main method that starts the Spring Boot application
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load(); //load .env file
    System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
    System.setProperty("DATABASE_USERNAME", dotenv.get("DATABASE_USERNAME"));
    System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));

    SpringApplication.run(UserApiApplication.class, args);
	}

}