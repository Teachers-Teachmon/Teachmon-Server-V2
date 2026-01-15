package solvit.teachmon.domain.auth.application.dto.response;

import org.springframework.http.ResponseCookie;

public record TokenResponseDto(
        String accessToken,
        ResponseCookie refreshTokenCookie
) {}