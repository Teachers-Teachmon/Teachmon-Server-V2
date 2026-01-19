package solvit.teachmon.domain.auth.infrastructure.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.ActiveProfiles;
import solvit.teachmon.domain.auth.domain.entity.TokenEntity;
import solvit.teachmon.domain.auth.domain.repository.TokenRepository;
import solvit.teachmon.domain.auth.exception.RefreshTokenNotFoundException;
import solvit.teachmon.global.properties.JwtProperties;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("JWT 매니저 테스트")
class JwtManagerTest {

    @Mock
    private TokenRepository tokenRepository;

    private JwtManager jwtManager;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties(
                "testsecretkeytestsecretkeytestsecretkey",
                1800000L,
                604800000L
        );
        jwtManager = new JwtManager(jwtProperties, tokenRepository);
    }

    @Test
    @DisplayName("액세스 토큰을 성공적으로 생성한다")
    void createAccessToken_Success() {
        // Given: 유효한 이메일이 주어졌을 때
        String mail = "test@example.com";

        // When: 액세스 토큰을 생성하면
        String accessToken = jwtManager.createAccessToken(mail);

        // Then: 유효한 JWT 토큰이 생성된다
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
        assertThat(accessToken.split("\\.")).hasSize(3); // JWT는 3부분으로 구성
    }

    @Test
    @DisplayName("리프레시 토큰을 성공적으로 생성하고 저장한다")
    void createRefreshToken_Success() {
        // Given: 유효한 이메일이 주어졌을 때
        String mail = "test@example.com";

        // When: 리프레시 토큰을 생성하면
        ResponseCookie refreshTokenCookie = jwtManager.createRefreshToken(mail);

        // Then: 유효한 리프레시 토큰 쿠키가 생성되고 저장된다
        assertThat(refreshTokenCookie).isNotNull();
        assertThat(refreshTokenCookie.getName()).isEqualTo("refresh_token");
        assertThat(refreshTokenCookie.getValue()).isNotEmpty();
        assertThat(refreshTokenCookie.getMaxAge()).isEqualTo(Duration.ofMillis(jwtProperties.getRefreshExpiration()));
        assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
        assertThat(refreshTokenCookie.isSecure()).isTrue();
        assertThat(refreshTokenCookie.getSameSite()).isEqualTo("Strict");
        
        then(tokenRepository).should(times(1)).save(any(TokenEntity.class));
    }

    @Test
    @DisplayName("존재하는 리프레시 토큰을 성공적으로 삭제한다")
    void deleteRefreshToken_Success() {
        // Given: 저장된 리프레시 토큰이 있을 때
        String refreshToken = "valid-refresh-token";
        given(tokenRepository.existsById(refreshToken)).willReturn(true);

        // When: 리프레시 토큰을 삭제하면
        jwtManager.deleteRefreshToken(refreshToken);

        // Then: 토큰이 삭제된다
        then(tokenRepository).should(times(1)).deleteById(refreshToken);
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰 삭제 시 예외가 발생한다")
    void deleteRefreshToken_NotFound() {
        // Given: 존재하지 않는 리프레시 토큰일 때
        String refreshToken = "non-existent-refresh-token";
        given(tokenRepository.existsById(refreshToken)).willReturn(false);

        // When & Then: 리프래시 토큰 삭제 시 예외가 발생한다
        assertThatThrownBy(() -> jwtManager.deleteRefreshToken(refreshToken))
                .isInstanceOf(RefreshTokenNotFoundException.class);
    }

    @Test
    @DisplayName("리프레시 토큰 쿠키를 성공적으로 삭제한다")
    void deleteRefreshTokenCookie_Success() {
        // Given: 저장된 리프레시 토큰이 있을 때
        String refreshToken = "valid-refresh-token";
        given(tokenRepository.existsById(refreshToken)).willReturn(true);

        // When: 리프래시 토큰 쿠키를 삭제하면
        ResponseCookie deletedCookie = jwtManager.deleteRefreshTokenCookie(refreshToken);

        // Then: 삭제된 쿠키가 반환되고 토큰이 삭제된다
        assertThat(deletedCookie).isNotNull();
        assertThat(deletedCookie.getName()).isEqualTo("refresh_token");
        assertThat(deletedCookie.getValue()).isEqualTo(refreshToken);
        assertThat(deletedCookie.getMaxAge()).isEqualTo(Duration.ZERO);
        assertThat(deletedCookie.isHttpOnly()).isTrue();
        assertThat(deletedCookie.isSecure()).isTrue();
        assertThat(deletedCookie.getSameSite()).isEqualTo("Strict");
        
        then(tokenRepository).should(times(1)).deleteById(refreshToken);
    }

    @Test
    @DisplayName("JWT 시크릿 키가 올바르게 설정된다")
    void jwtSecretKeyIsSetCorrectly() {
        // Given: JWT 매니저가 생성되었을 때

        // When: 액세스 토큰을 생성하면
        String accessToken = jwtManager.createAccessToken("test@example.com");
        
        // Then: 유효한 JWT 토큰이 생성된다
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
    }
}