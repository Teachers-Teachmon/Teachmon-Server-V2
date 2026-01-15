package solvit.teachmon.domain.oauth2.infrastructure.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.oauth2.infrastructure.jwt.JwtProvider;
import solvit.teachmon.domain.oauth2.infrastructure.security.vo.TeachmonOAuth2User;
import solvit.teachmon.global.properties.WebProperties;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TeachmonOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final WebProperties webProperties;

    @Override
    public void onAuthenticationSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Authentication authentication
    ) throws IOException {
        TeachmonOAuth2User oauth2User = (TeachmonOAuth2User) authentication.getPrincipal();
        String mail = Objects.requireNonNull(oauth2User).getName();

        String accessToken = jwtProvider.createAccessToken(mail);

        ResponseCookie refreshTokenCookie = jwtProvider.createRefreshToken(mail);
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        String frontendUrl = webProperties.getFrontEndUrl() + "/oauth2/callback#access_token=" + accessToken;
        response.sendRedirect(frontendUrl);
    }
}
