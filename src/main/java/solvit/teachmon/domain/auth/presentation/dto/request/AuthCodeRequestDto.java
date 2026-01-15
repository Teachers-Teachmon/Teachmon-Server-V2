package solvit.teachmon.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record AuthCodeRequestDto(
    @NotNull String code
) {}
