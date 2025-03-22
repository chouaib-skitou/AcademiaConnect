package com.academiaconnect.auth.authservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {

    public static void main(String[] args) {
        // Load .env file
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
        System.out.println("JWT_SECRET loaded: " + dotenv.get("JWT_SECRET"));
        System.out.println("JWT_REFRESH_SECRET loaded: " + dotenv.get("JWT_REFRESH_SECRET"));

        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
