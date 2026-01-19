package solvit.teachmon.domain.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.auth.domain.entity.AuthCodeEntity;
import solvit.teachmon.domain.auth.domain.repository.AuthCodeRepository;
import solvit.teachmon.domain.auth.exception.AuthCodeNotFoundException;
import solvit.teachmon.domain.auth.infrastructure.jwt.JwtManager;
import solvit.teachmon.domain.auth.presentation.dto.response.TokenResponseDto;
import solvit.teachmon.global.security.jwt.JwtValidator;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtManager jwtManager;
    private final JwtValidator jwtValidator;
    private final AuthCodeRepository authCodeRepository;

    public ResponseCookie deleteRefreshToken(String refreshToken) {
        return jwtManager.deleteRefreshTokenCookie(refreshToken);
    }

    public TokenResponseDto reissueToken(String refreshToken) {
        String mail = jwtValidator.getMailFromToken(refreshToken);

        jwtManager.deleteRefreshToken(refreshToken);

        String newAccessToken = jwtManager.createAccessToken(mail);
        ResponseCookie newRefreshTokenCookie = jwtManager.createRefreshToken(mail);

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshTokenCookie(newRefreshTokenCookie)
                .build();
    }

    public String getAccessTokenByAuthCode(String authCode) {
        AuthCodeEntity authCodeEntity = authCodeRepository.findByAuthCode(authCode).orElseThrow(AuthCodeNotFoundException::new);
        String accessToken = authCodeEntity.getAccessToken();
        authCodeRepository.delete(authCodeEntity);
        return accessToken;
    }
}

