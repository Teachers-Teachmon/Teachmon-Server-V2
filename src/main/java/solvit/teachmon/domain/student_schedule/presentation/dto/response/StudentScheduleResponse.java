package solvit.teachmon.domain.student_schedule.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

@Builder
public record StudentScheduleResponse(
        @JsonProperty("student_id")
        Long studentId,
        Integer number,
        String name,
        ScheduleType state,
        @JsonProperty("schedule_id")
        Long scheduleId
) {}
