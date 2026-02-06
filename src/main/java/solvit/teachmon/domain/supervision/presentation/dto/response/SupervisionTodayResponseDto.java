package solvit.teachmon.domain.supervision.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionTodayType;

@Builder
public record SupervisionTodayResponseDto(
        SupervisionTodayType type
) {}