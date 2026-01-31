package solvit.teachmon.global.security.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.global.constants.JwtConstants;
import solvit.teachmon.global.properties.JwtProperties;
import solvit.teachmon.global.security.exception.InvalidJsonWebTokenException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT 검증기 테스트")
class JwtValidatorTest {

    @Mock
    private JwtProperties jwtProperties;

    private JwtValidator jwtValidator;
    private SecretKey secretKey;
    private String validToken;

    @BeforeEach
    void setUp() {
        String jwtSecret = "testsecretkeytestsecretkeytestsecretkey";
        given(jwtProperties.getSecret()).willReturn(jwtSecret);
        
        jwtValidator = new JwtValidator(jwtProperties);
        
        secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm()
        );
        
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
        String authHeader = JwtConstants.AUTHORIZATION_HEADER_PREFIX + validToken;

        String mail = jwtValidator.getMailFromAuthorizationHeader(authHeader);

        assertThat(mail).isEqualTo("kim@teacher.com");
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 유효하지 않다고 판단한다")
    void shouldReturnTrueWhenAuthorizationHeaderIsNull() {
        
        boolean isInvalid = jwtValidator.isInvalidAuthorizationHeader(null);

        assertThat(isInvalid).isTrue();
    }

    @Test
    @DisplayName("Bearer로 시작하지 않는 헤더는 유효하지 않다고 판단한다")
    void shouldReturnTrueWhenAuthorizationHeaderDoesNotStartWithBearer() {
        String invalidHeader = "Basic " + validToken;

        boolean isInvalid = jwtValidator.isInvalidAuthorizationHeader(invalidHeader);

        assertThat(isInvalid).isTrue();
    }

    @Test
    @DisplayName("유효한 Authorization 헤더는 올바르다고 판단한다")
    void shouldReturnFalseWhenAuthorizationHeaderIsValid() {
        String validHeader = JwtConstants.AUTHORIZATION_HEADER_PREFIX + validToken;

        boolean isInvalid = jwtValidator.isInvalidAuthorizationHeader(validHeader);

        assertThat(isInvalid).isFalse();
    }

    @Test
    @DisplayName("잘못된 발급자의 토큰이면 예외가 발생한다")
    void shouldThrowExceptionWhenTokenHasInvalidIssuer() {
        String invalidToken = Jwts.builder()
                .subject("kim@teacher.com")
                .claim(JwtConstants.CLAIM_MADE_BY_KEY, "invalid-issuer")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
        String authHeader = JwtConstants.AUTHORIZATION_HEADER_PREFIX + invalidToken;

        assertThatThrownBy(() -> jwtValidator.getMailFromAuthorizationHeader(authHeader))
                .isInstanceOf(InvalidJsonWebTokenException.class);
    }

    @Test
    @DisplayName("빈 Authorization 헤더는 유효하지 않다고 판단한다")
    void shouldReturnTrueWhenAuthorizationHeaderIsEmpty() {
        String emptyHeader = "";

        boolean isInvalid = jwtValidator.isInvalidAuthorizationHeader(emptyHeader);

        assertThat(isInvalid).isTrue();
    }

    @Test
    @DisplayName("토큰에서 올바른 subject를 추출할 수 있다")
    void shouldExtractSubjectFromToken() {
        String authHeader = JwtConstants.AUTHORIZATION_HEADER_PREFIX + validToken;

        String mail = jwtValidator.getMailFromAuthorizationHeader(authHeader);

        assertThat(mail).isNotNull();
        assertThat(mail).contains("@");
    }
}
