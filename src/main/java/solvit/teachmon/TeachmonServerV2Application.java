package solvit.teachmon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TeachmonServerV2Application {

    public static void main(String[] args) {
        SpringApplication.run(TeachmonServerV2Application.class, args);
    }

}
