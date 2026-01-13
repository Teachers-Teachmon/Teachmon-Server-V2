package solvit.teachmon.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

@Configuration
@Profile("test")
public class H2ServerConfig {

    @PostConstruct
    public void configureH2Memory() {
        System.out.println("H2 Memory Database configured for testing!");
        System.out.println("Database URL: jdbc:h2:mem:testdb");
        System.out.println("Using in-memory database - no persistent storage");
    }
}