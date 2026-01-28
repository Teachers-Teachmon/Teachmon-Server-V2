package solvit.teachmon.domain.student_schedule.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

@QueryProjection
public record StudentScheduleDto(
        Long studentId,
        Integer grade,
        Integer classNumber,
        Integer number,
        String name,

        LocalDate day,
        SchoolPeriod period,
        Long scheduleId,
        ScheduleType state
) {
}
