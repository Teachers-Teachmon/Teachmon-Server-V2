package solvit.teachmon.domain.auth.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import solvit.teachmon.domain.auth.application.dto.response.TokenResponseDto;
import solvit.teachmon.domain.auth.application.service.AuthService;
import solvit.teachmon.domain.auth.exception.RefreshTokenNotFoundException;
import solvit.teachmon.domain.auth.presentation.dto.request.AuthCodeRequestDto;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Test
    @DisplayName("리프레시 토큰으로 새로운 액세스 토큰을 재발급한다")
    void reissueToken() {
        // given
        String refreshToken = "test-refresh-token";
        String newAccessToken = "new-access-token";
        ResponseCookie newRefreshCookie = ResponseCookie.from("refresh_token", "new-refresh-token").build();
        TokenResponseDto tokenResponseDto = new TokenResponseDto(newAccessToken, newRefreshCookie);

        given(authService.reissueToken(refreshToken)).willReturn(tokenResponseDto);

        // when
        ResponseEntity<Map<String, String>> response = authController.reissue(refreshToken);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsEntry("access_token", newAccessToken);
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotNull();
    }

    @Test
    @DisplayName("리프레시 토큰이 없으면 재발급 시 예외가 발생한다")
    void reissueTokenWithoutRefreshToken() {
        // when & then
        assertThatThrownBy(() -> authController.reissue(null))
                .isInstanceOf(RefreshTokenNotFoundException.class);
    }

    @Test
    @DisplayName("로그아웃 시 리프레시 토큰을 삭제한다")
    void logout() {
        // given
        String refreshToken = "test-refresh-token";
        ResponseCookie deletedCookie = ResponseCookie.from("refresh_token", "")
                .maxAge(0)
                .build();

        given(authService.deleteRefreshToken(refreshToken)).willReturn(deletedCookie);

        // when
        ResponseEntity<Void> response = authController.logout(refreshToken);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().get(HttpHeaders.SET_COOKIE)).isNotNull();
        then(authService).should(times(1)).deleteRefreshToken(refreshToken);
    }

    @Test
    @DisplayName("인증 코드로 액세스 토큰을 발급받는다")
    void authCode() {
        // given
        String authCode = "test-auth-code";
        String accessToken = "test-access-token";
        AuthCodeRequestDto requestDto = new AuthCodeRequestDto(authCode);

        given(authService.getAccessTokenByAuthCode(authCode)).willReturn(accessToken);

        // when
        ResponseEntity<Map<String, String>> response = authController.authCode(requestDto);

        // then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsEntry("access_token", accessToken);
    }
}
