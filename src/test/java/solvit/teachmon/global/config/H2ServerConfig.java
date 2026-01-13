package solvit.teachmon.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

@Configuration
@Profile("test")
public class H2ServerConfig {

    private Process h2Process;

    @PostConstruct
    public void startH2Server() {
        try {
            // H2 서버를 별도 프로세스로 시작
            ProcessBuilder pb = new ProcessBuilder(
                "java", "-cp", "h2-2.2.224.jar", 
                "org.h2.tools.Server",
                "-tcp", "-tcpPort", "9092",
                "-web", "-webPort", "8082", 
                "-webAllowOthers", "-tcpAllowOthers"
            );
            h2Process = pb.start();
            
            // 서버가 시작될 시간을 줍니다
            Thread.sleep(2000);
            System.out.println("H2 Server started automatically!");
            System.out.println("Web Console: http://localhost:8082");
            System.out.println("JDBC URL: jdbc:h2:tcp://localhost:9092/~/testdb");
            
        } catch (IOException | InterruptedException e) {
            System.out.println("H2 server may already be running or failed to start: " + e.getMessage());
        }
    }

    @PreDestroy
    public void stopH2Server() {
        if (h2Process != null && h2Process.isAlive()) {
            h2Process.destroy();
            try {
                h2Process.waitFor();
                System.out.println("H2 Server stopped automatically!");
            } catch (InterruptedException e) {
                h2Process.destroyForcibly();
                System.out.println("H2 Server forcibly stopped!");
            }
        }
    }
}