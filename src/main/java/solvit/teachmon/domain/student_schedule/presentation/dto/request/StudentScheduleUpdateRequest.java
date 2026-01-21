package solvit.teachmon.domain.student_schedule.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

public record StudentScheduleUpdateRequest(
        @NotNull(message = "학생 스케줄 변경시 state(상태)는 필수입니다.")
        ScheduleType state
) {}
