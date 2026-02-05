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
        String mail = "test@example.com";

        String accessToken = jwtManager.createAccessToken(mail);

        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
        assertThat(accessToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("리프레시 토큰을 성공적으로 생성하고 저장한다")
    void createRefreshToken_Success() {
        String mail = "test@example.com";

        ResponseCookie refreshTokenCookie = jwtManager.createRefreshToken(mail);

        assertThat(refreshTokenCookie).isNotNull();
        assertThat(refreshTokenCookie.getName()).isEqualTo("refresh_token");
        assertThat(refreshTokenCookie.getValue()).isNotEmpty();
        assertThat(refreshTokenCookie.getMaxAge()).isEqualTo(Duration.ofMillis(jwtProperties.getRefreshExpiration()));
        assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
        assertThat(refreshTokenCookie.isSecure()).isTrue();
        assertThat(refreshTokenCookie.getSameSite()).isEqualTo("NONE");
        
        then(tokenRepository).should(times(1)).save(any(TokenEntity.class));
    }

    @Test
    @DisplayName("존재하는 리프레시 토큰을 성공적으로 삭제한다")
    void deleteRefreshToken_Success() {
        String refreshToken = "valid-refresh-token";
        given(tokenRepository.existsById(refreshToken)).willReturn(true);

        jwtManager.deleteRefreshToken(refreshToken);

        then(tokenRepository).should(times(1)).deleteById(refreshToken);
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰 삭제 시 예외가 발생한다")
    void deleteRefreshToken_NotFound() {
        String refreshToken = "non-existent-refresh-token";
        given(tokenRepository.existsById(refreshToken)).willReturn(false);

        assertThatThrownBy(() -> jwtManager.deleteRefreshToken(refreshToken))
                .isInstanceOf(RefreshTokenNotFoundException.class);
    }

    @Test
    @DisplayName("리프레시 토큰 쿠키를 성공적으로 삭제한다")
    void deleteRefreshTokenCookie_Success() {
        String refreshToken = "valid-refresh-token";
        given(tokenRepository.existsById(refreshToken)).willReturn(true);

        ResponseCookie deletedCookie = jwtManager.deleteRefreshTokenCookie(refreshToken);

        assertThat(deletedCookie).isNotNull();
        assertThat(deletedCookie.getName()).isEqualTo("refresh_token");
        assertThat(deletedCookie.getValue()).isEqualTo(refreshToken);
        assertThat(deletedCookie.getMaxAge()).isEqualTo(Duration.ZERO);
        assertThat(deletedCookie.isHttpOnly()).isTrue();
        assertThat(deletedCookie.isSecure()).isTrue();
        assertThat(deletedCookie.getSameSite()).isEqualTo("NONE");
        
        then(tokenRepository).should(times(1)).deleteById(refreshToken);
    }

    @Test
    @DisplayName("JWT 시크릿 키가 올바르게 설정된다")
    void jwtSecretKeyIsSetCorrectly() {

        String accessToken = jwtManager.createAccessToken("test@example.com");
        
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();
    }
}
