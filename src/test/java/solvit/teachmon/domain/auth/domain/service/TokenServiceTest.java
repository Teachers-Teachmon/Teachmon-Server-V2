package solvit.teachmon.domain.auth.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.auth.domain.entity.TokenEntity;
import solvit.teachmon.domain.auth.domain.repository.TokenRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    @Test
    @DisplayName("리프레시 토큰을 저장한다")
    void saveToken() {
        // given
        String token = "test-refresh-token";
        String mail = "test@example.com";
        Long expiration = 604800000L;

        // when
        tokenService.saveToken(token, mail, expiration);

        // then
        then(tokenRepository).should(times(1)).save(any(TokenEntity.class));
    }

    @Test
    @DisplayName("리프레시 토큰을 삭제한다")
    void deleteToken() {
        // given
        String token = "test-refresh-token";

        // when
        tokenService.deleteToken(token);

        // then
        then(tokenRepository).should(times(1)).deleteById(token);
    }

    @Test
    @DisplayName("유효하지 않은 토큰인지 확인한다 - 존재하지 않는 경우")
    void isInvalidTokenWhenNotExists() {
        // given
        String token = "invalid-token";
        given(tokenRepository.existsById(token)).willReturn(false);

        // when
        boolean result = tokenService.isInvalidToken(token);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유효한 토큰인지 확인한다 - 존재하는 경우")
    void isValidTokenWhenExists() {
        // given
        String token = "valid-token";
        given(tokenRepository.existsById(token)).willReturn(true);

        // when
        boolean result = tokenService.isInvalidToken(token);

        // then
        assertThat(result).isFalse();
    }
}
