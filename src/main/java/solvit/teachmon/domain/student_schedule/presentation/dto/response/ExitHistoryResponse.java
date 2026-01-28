package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

@Builder
public record ExitHistoryResponse(
        @JsonProperty("exit_id")
        Long exitId,

        @JsonProperty("day")
        LocalDate day,

        @JsonProperty("teacher")
        String teacher,

        @JsonProperty("number")
        Integer number,

        @JsonProperty("name")
        String name,

        @JsonProperty("period")
        SchoolPeriod period
) {
}
