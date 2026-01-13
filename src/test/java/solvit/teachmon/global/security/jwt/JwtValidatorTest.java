package solvit.teachmon.global.security.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import solvit.teachmon.global.constants.JwtConstants;
import solvit.teachmon.global.properties.JwtProperties;
import solvit.teachmon.global.security.exception.InvalidJsonWebTokenException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("JWT 검증기 테스트")
class JwtValidatorTest {

    private JwtValidator jwtValidator;

    private SecretKey secretKey;
    private String validToken;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @BeforeEach
    void setUp() {
        secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        
        // JwtProperties 생성
        JwtProperties jwtProperties = new JwtProperties(jwtSecret, 3600000L, 86400000L);
        
        // JwtValidator 직접 생성
        jwtValidator = new JwtValidator(jwtProperties);
        
        // 유효한 토큰 생성
        validToken = Jwts.builder()
                .subject("kim@teacher.com")
                .claim(JwtConstants.CLAIM_MADE_BY_KEY, JwtConstants.CLAIM_MADE_BY_VALUE)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
    }

    @Test
    @DisplayName("유효한 Authorization 헤더에서 메일을 추출할 수 있다")
    void shouldExtractMailFromValidAuthorizationHeader() {
        // Given: 유효한 Authorization 헤더가 있을 때
        String authHeader = JwtConstants.AUTHORIZATION_HEADER_PREFIX + validToken;

        // When: 메일을 추출하면
        String mail = jwtValidator.getMailFromAuthorizationHeader(authHeader);

        // Then: 올바른 메일이 반환된다
        assertThat(mail).isEqualTo("kim@teacher.com");
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 유효하지 않다고 판단한다")
    void shouldReturnTrueWhenAuthorizationHeaderIsNull() {
        // Given: Authorization 헤더가 없을 때
        
        // When: 헤더 유효성을 검사하면
        boolean isInvalid = jwtValidator.isInvalidAuthorizationHeader(null);

        // Then: 유효하지 않다고 판단한다
        assertThat(isInvalid).isTrue();
    }

    @Test
    @DisplayName("Bearer로 시작하지 않는 헤더는 유효하지 않다고 판단한다")
    void shouldReturnTrueWhenAuthorizationHeaderDoesNotStartWithBearer() {
        // Given: Bearer로 시작하지 않는 헤더가 있을 때
        String invalidHeader = "Basic " + validToken;

        // When: 헤더 유효성을 검사하면
        boolean isInvalid = jwtValidator.isInvalidAuthorizationHeader(invalidHeader);

        // Then: 유효하지 않다고 판단한다
        assertThat(isInvalid).isTrue();
    }

    @Test
    @DisplayName("유효한 Authorization 헤더는 올바르다고 판단한다")
    void shouldReturnFalseWhenAuthorizationHeaderIsValid() {
        // Given: 유효한 Authorization 헤더가 있을 때
        String validHeader = JwtConstants.AUTHORIZATION_HEADER_PREFIX + validToken;

        // When: 헤더 유효성을 검사하면
        boolean isInvalid = jwtValidator.isInvalidAuthorizationHeader(validHeader);

        // Then: 유효하다고 판단한다
        assertThat(isInvalid).isFalse();
    }

    @Test
    @DisplayName("잘못된 발급자의 토큰이면 예외가 발생한다")
    void shouldThrowExceptionWhenTokenHasInvalidIssuer() {
        // Given: 잘못된 발급자 정보가 있는 토큰이 있을 때
        String invalidToken = Jwts.builder()
                .subject("kim@teacher.com")
                .claim(JwtConstants.CLAIM_MADE_BY_KEY, "invalid-issuer")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
        String authHeader = JwtConstants.AUTHORIZATION_HEADER_PREFIX + invalidToken;

        // When & Then: 메일을 추출하면 예외가 발생한다
        assertThatThrownBy(() -> jwtValidator.getMailFromAuthorizationHeader(authHeader))
                .isInstanceOf(InvalidJsonWebTokenException.class);
    }
}