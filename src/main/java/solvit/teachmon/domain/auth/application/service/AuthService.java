package solvit.teachmon.domain.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.auth.application.dto.response.TokenResponseDto;
import solvit.teachmon.domain.auth.domain.service.AuthCodeService;
import solvit.teachmon.domain.auth.infrastructure.jwt.JwtManager;
import solvit.teachmon.global.security.jwt.JwtValidator;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtManager jwtManager;
    private final JwtValidator jwtValidator;
    private final AuthCodeService authCodeService;

    public ResponseCookie deleteRefreshToken(String refreshToken) {
        return jwtManager.deleteRefreshTokenCookie(refreshToken);
    }

    public TokenResponseDto reissueToken(String refreshToken) {
        String mail = jwtValidator.getMailFromToken(refreshToken);

        jwtManager.deleteRefreshToken(refreshToken);

        String newAccessToken = jwtManager.createAccessToken(mail);
        ResponseCookie newRefreshTokenCookie = jwtManager.createRefreshToken(mail);

        return new TokenResponseDto(newAccessToken, newRefreshTokenCookie);
    }

    public String getAccessTokenByAuthCode(String authCode) {
        String accessToken = authCodeService.getAccessTokenByAuthCode(authCode);
        authCodeService.delete(authCode);
        return accessToken;
    }
}

