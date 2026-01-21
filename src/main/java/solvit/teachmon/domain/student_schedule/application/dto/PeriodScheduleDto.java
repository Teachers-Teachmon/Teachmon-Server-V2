package solvit.teachmon.domain.student_schedule.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.global.enums.SchoolPeriod;

@QueryProjection
public record PeriodScheduleDto(
        SchoolPeriod period,
        ScheduleType type
) {}
