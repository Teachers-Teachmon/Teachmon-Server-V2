package solvit.teachmon.domain.auth.infrastructure.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.auth.domain.service.TokenService;
import solvit.teachmon.domain.auth.exception.RefreshTokenNotFoundException;
import solvit.teachmon.global.constants.JwtConstants;
import solvit.teachmon.global.properties.JwtProperties;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class JwtManager {
    private final JwtProperties jwtProperties;
    private final TokenService tokenService;
    private final SecretKey secretKey;

    @Autowired
    public JwtManager(JwtProperties jwtProperties, TokenService tokenService) {
        this.jwtProperties = jwtProperties;
        this.tokenService = tokenService;
        this.secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String createAccessToken(String mail) {
        long now = System.currentTimeMillis();
        Date expiration = new Date(now + jwtProperties.getAccessExpiration());
        
        return Jwts.builder()
                .subject(mail)
                .claim(JwtConstants.CLAIM_MADE_BY_KEY, JwtConstants.CLAIM_MADE_BY_VALUE)
                .issuedAt(new Date(now))
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public ResponseCookie createRefreshToken(String mail) {
        long now = System.currentTimeMillis();
        Date expiration = new Date(now + jwtProperties.getRefreshExpiration());

        String refreshToken = Jwts.builder()
                .subject(mail)
                .claim(JwtConstants.CLAIM_MADE_BY_KEY, JwtConstants.CLAIM_MADE_BY_VALUE)
                .issuedAt(new Date(now))
                .expiration(expiration)
                .signWith(secretKey)
                .compact();

        tokenService.saveToken(refreshToken, mail, jwtProperties.getRefreshExpiration());

        return ResponseCookie.from("refresh_token", refreshToken)
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshExpiration()))
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie(String refreshToken) {
        deleteRefreshToken(refreshToken);

        return ResponseCookie.from("refresh_token", "")
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    public void deleteRefreshToken(String refreshToken) {
        if(tokenService.isInvalidToken(refreshToken)) throw new RefreshTokenNotFoundException();
        tokenService.deleteToken(refreshToken);
    }
}
