package solvit.teachmon.domain.team.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record TeamDeleteRequestDto(
        @NotNull(message = "팀 삭제 요청에서 id(아이디)는 필수입니다.")
        Long id
) {}
