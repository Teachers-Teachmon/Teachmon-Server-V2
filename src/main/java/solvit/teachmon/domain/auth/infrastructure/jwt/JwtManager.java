package solvit.teachmon.domain.auth.infrastructure.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.auth.domain.entity.TokenEntity;
import solvit.teachmon.domain.auth.domain.repository.TokenRepository;
import solvit.teachmon.domain.auth.exception.RefreshTokenNotFoundException;
import solvit.teachmon.global.annotation.Trace;
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
    private final TokenRepository tokenRepository;
    private final SecretKey secretKey;

    @Autowired
    public JwtManager(JwtProperties jwtProperties, TokenRepository tokenRepository) {
        this.jwtProperties = jwtProperties;
        this.tokenRepository = tokenRepository;
        this.secretKey = new SecretKeySpec(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
    }

    @Trace
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

        saveRefreshToken(refreshToken, mail);

        return ResponseCookie.from(JwtConstants.REFRESH_TOKEN_COOKIE_HEADER, refreshToken)
                .maxAge(Duration.ofMillis(jwtProperties.getRefreshExpiration()))
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite(Cookie.SameSite.NONE.toString())
                .build();
    }

    public ResponseCookie deleteRefreshTokenCookie(String refreshToken) {
        deleteRefreshToken(refreshToken);

        return ResponseCookie.from(JwtConstants.REFRESH_TOKEN_COOKIE_HEADER, refreshToken)
                .maxAge(0)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite(Cookie.SameSite.NONE.toString())
                .build();
    }

    public void deleteRefreshToken(String refreshToken) {
        if(!tokenRepository.existsById(refreshToken)) throw new RefreshTokenNotFoundException();
        tokenRepository.deleteById(refreshToken);
    }

    private void saveRefreshToken(String refreshToken, String mail) {
        TokenEntity tokenEntity = TokenEntity.builder()
                .token(refreshToken)
                .mail(mail)
                .expiration(jwtProperties.getRefreshExpiration())
                .build();
        tokenRepository.save(tokenEntity);
    }
}
