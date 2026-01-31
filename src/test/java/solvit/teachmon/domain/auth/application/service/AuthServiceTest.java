package solvit.teachmon.domain.auth.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import solvit.teachmon.domain.auth.presentation.dto.response.TokenResponseDto;
import solvit.teachmon.domain.auth.domain.entity.AuthCodeEntity;
import solvit.teachmon.domain.auth.domain.repository.AuthCodeRepository;
import solvit.teachmon.domain.auth.exception.AuthCodeNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import solvit.teachmon.domain.auth.infrastructure.jwt.JwtManager;
import solvit.teachmon.global.security.jwt.JwtValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtManager jwtManager;

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private AuthCodeRepository authCodeRepository;

    @Test
    @DisplayName("리프레시 토큰을 삭제하고 쿠키를 반환한다")
    void deleteRefreshToken() {
        String refreshToken = "test-refresh-token";
        ResponseCookie expectedCookie = ResponseCookie.from("refresh_token", "")
                .maxAge(0)
                .build();
        given(jwtManager.deleteRefreshTokenCookie(refreshToken)).willReturn(expectedCookie);

        ResponseCookie actualCookie = authService.deleteRefreshToken(refreshToken);

        assertThat(actualCookie).isEqualTo(expectedCookie);
        then(jwtManager).should(times(1)).deleteRefreshTokenCookie(refreshToken);
    }

    @Test
    @DisplayName("리프레시 토큰으로 새로운 액세스 토큰과 리프레시 토큰을 발급한다")
    void reissueToken() {
        String refreshToken = "test-refresh-token";
        String mail = "test@example.com";
        String newAccessToken = "new-access-token";
        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refresh_token", "new-refresh-token").build();

        given(jwtValidator.getMailFromToken(refreshToken)).willReturn(mail);
        given(jwtManager.createAccessToken(mail)).willReturn(newAccessToken);
        given(jwtManager.createRefreshToken(mail)).willReturn(newRefreshTokenCookie);

        TokenResponseDto result = authService.reissueToken(refreshToken);

        assertThat(result.accessToken()).isEqualTo(newAccessToken);
        assertThat(result.refreshTokenCookie()).isEqualTo(newRefreshTokenCookie);
        then(jwtManager).should(times(1)).deleteRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("인증 코드로 액세스 토큰을 조회하고 코드를 삭제한다")
    void getAccessTokenByAuthCode_Success() {
        String authCode = "test-auth-code";
        String expectedAccessToken = "test-access-token";
        AuthCodeEntity authCodeEntity = AuthCodeEntity.builder()
                .authCode(authCode)
                .accessToken(expectedAccessToken)
                .timeToLive(300L)
                .build();
        given(authCodeRepository.findByAuthCode(authCode)).willReturn(Optional.of(authCodeEntity));

        String actualAccessToken = authService.getAccessTokenByAuthCode(authCode);

        assertThat(actualAccessToken).isEqualTo(expectedAccessToken);
        then(authCodeRepository).should(times(1)).delete(authCodeEntity);
    }

    @Test
    @DisplayName("존재하지 않는 인증 코드로 조회시 예외가 발생한다")
    void getAccessTokenByAuthCode_NotFound() {
        String authCode = "non-existent-auth-code";
        given(authCodeRepository.findByAuthCode(authCode)).willReturn(Optional.empty());

        assertThrows(AuthCodeNotFoundException.class, () -> authService.getAccessTokenByAuthCode(authCode));
    }
}
