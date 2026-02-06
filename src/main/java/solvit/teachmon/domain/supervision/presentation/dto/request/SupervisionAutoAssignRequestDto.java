package solvit.teachmon.domain.supervision.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SupervisionAutoAssignRequestDto(
        @JsonProperty("start_day")
        @NotNull(message = "시작 날짜는 필수입니다.")
        LocalDate startDay,

        @JsonProperty("end_day")
        @NotNull(message = "종료 날짜는 필수입니다.")
        LocalDate endDay
) {
}