package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

@Builder
public record PeriodScheduleResponse(
        @JsonProperty("schedule_id")
        Long scheduleId,

        ScheduleType state
) {}
