package solvit.teachmon.domain.auth.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.auth.domain.entity.AuthCodeEntity;
import solvit.teachmon.domain.auth.domain.repository.AuthCodeRepository;
import solvit.teachmon.domain.auth.exception.AuthCodeNotFoundException;
import solvit.teachmon.global.properties.AuthCodeProperties;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AuthCodeServiceTest {

    @InjectMocks
    private AuthCodeService authCodeService;

    @Mock
    private AuthCodeRepository authCodeRepository;

    @Mock
    private AuthCodeProperties authCodeProperties;

    @Test
    @DisplayName("인증 코드와 액세스 토큰으로 AuthCode를 생성한다")
    void createAuthCode() {
        // given
        String authCode = "test-auth-code";
        String accessToken = "test-access-token";
        Long expiration = 300000L;
        given(authCodeProperties.getExpiration()).willReturn(expiration);

        // when
        authCodeService.create(authCode, accessToken);

        // then
        then(authCodeRepository).should(times(1)).save(any(AuthCodeEntity.class));
    }

    @Test
    @DisplayName("인증 코드로 액세스 토큰을 조회한다")
    void getAccessTokenByAuthCode() {
        // given
        String authCode = "test-auth-code";
        String expectedAccessToken = "test-access-token";
        AuthCodeEntity authCodeEntity = AuthCodeEntity.builder()
                .authCode(authCode)
                .accessToken(expectedAccessToken)
                .timeToLive(300000L)
                .build();
        given(authCodeRepository.findById(authCode)).willReturn(Optional.of(authCodeEntity));

        // when
        String actualAccessToken = authCodeService.getAccessTokenByAuthCode(authCode);

        // then
        assertThat(actualAccessToken).isEqualTo(expectedAccessToken);
    }

    @Test
    @DisplayName("존재하지 않는 인증 코드 조회 시 예외가 발생한다")
    void getAccessTokenByAuthCodeNotFound() {
        // given
        String authCode = "invalid-auth-code";
        given(authCodeRepository.findById(authCode)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authCodeService.getAccessTokenByAuthCode(authCode))
                .isInstanceOf(AuthCodeNotFoundException.class);
    }

    @Test
    @DisplayName("인증 코드를 삭제한다")
    void deleteAuthCode() {
        // given
        String authCode = "test-auth-code";

        // when
        authCodeService.delete(authCode);

        // then
        then(authCodeRepository).should(times(1)).deleteById(authCode);
    }
}
