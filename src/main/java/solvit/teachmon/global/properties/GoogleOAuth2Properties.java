package solvit.teachmon.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth2")
public class GoogleOAuth2Properties {
    private final String loginUrl;
    private final String tokenUrl;
    private final String userInfoUrl;
    private final String clientId;
    private final String redirectUrl;
    private final String clientSecret;
    private final String grantType;
}
