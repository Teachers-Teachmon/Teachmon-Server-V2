package solvit.teachmon.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record AuthCodeRequestDto(
    @NotNull(message = "인증 요청에서 code(인증 코드)는 필수입니다.") String code
) {}
