package solvit.teachmon.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "code.auth")
public class AuthCodeProperties {
    private final Long expiration;
}
