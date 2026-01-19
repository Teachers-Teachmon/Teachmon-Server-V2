package solvit.teachmon.domain.auth.infrastructure.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.auth.domain.entity.AuthCodeEntity;
import solvit.teachmon.domain.auth.domain.repository.AuthCodeRepository;
import solvit.teachmon.domain.auth.infrastructure.jwt.JwtManager;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2User;
import solvit.teachmon.global.properties.AuthCodeProperties;
import solvit.teachmon.global.properties.WebProperties;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeachmonOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtManager jwtManager;
    private final AuthCodeProperties authCodeProperties;
    private final AuthCodeRepository authCodeRepository;
    private final WebProperties webProperties;

    @Override
    public void onAuthenticationSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Authentication authentication
    ) throws IOException {
        TeachmonOAuth2User oauth2User = (TeachmonOAuth2User) authentication.getPrincipal();
        String mail = Objects.requireNonNull(oauth2User).getName();

        String accessToken = jwtManager.createAccessToken(mail);
        String authCode = UUID.randomUUID().toString();
        createAuthCode(authCode, accessToken);

        ResponseCookie refreshTokenCookie = jwtManager.createRefreshToken(mail);
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        log.info(refreshTokenCookie.toString());

        String frontendUrl = webProperties.getFrontEndUrl() + "/oauth2/callback#code=" + authCode;
        response.sendRedirect(frontendUrl);
    }

    private void createAuthCode(String authCode, String accessToken) {
        AuthCodeEntity authCodeEntity = AuthCodeEntity.builder()
                .authCode(authCode)
                .accessToken(accessToken)
                .timeToLive(authCodeProperties.getExpiration())
                .build();
        authCodeRepository.save(authCodeEntity);
    }
}
