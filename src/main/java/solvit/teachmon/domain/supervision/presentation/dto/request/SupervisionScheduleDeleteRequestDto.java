package solvit.teachmon.domain.supervision.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionDeleteType;

import java.time.LocalDate;

@Builder
public record SupervisionScheduleDeleteRequestDto(
        @JsonProperty("day")
        @NotNull(message = "감독 일정 날짜는 필수입니다.")
        LocalDate day,

        @JsonProperty("type")
        @NotNull(message = "감독 일정 유형은 필수입니다.")
        SupervisionDeleteType type
) {}