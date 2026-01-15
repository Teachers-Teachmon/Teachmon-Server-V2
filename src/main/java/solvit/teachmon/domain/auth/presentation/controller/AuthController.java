package solvit.teachmon.domain.auth.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solvit.teachmon.domain.auth.application.dto.response.TokenResponseDto;
import solvit.teachmon.domain.auth.application.service.AuthService;
import solvit.teachmon.domain.auth.exception.RefreshTokenNotFoundException;
import solvit.teachmon.domain.auth.presentation.dto.request.AuthCodeRequestDto;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/reissue")
    public ResponseEntity<Map<String, String>> reissue(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if(refreshToken == null) throw new RefreshTokenNotFoundException();
        TokenResponseDto tokenResponseDto = authService.reissueToken(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenResponseDto.refreshTokenCookie().toString())
                .body(Map.of("access_token", tokenResponseDto.accessToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if(refreshToken == null) throw new RefreshTokenNotFoundException();
        ResponseCookie deletedRefreshTokenCookie = authService.deleteRefreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deletedRefreshTokenCookie.toString())
                .build();
    }

    @PostMapping("/code")
    public ResponseEntity<Map<String, String>> authCode(@RequestBody @Valid AuthCodeRequestDto authCodeRequestDto) {
        String accessToken = authService.getAccessTokenByAuthCode(authCodeRequestDto.code());
        return ResponseEntity.ok().body(Map.of("access_token", accessToken));
    }
}
