package solvit.teachmon.domain.student_schedule.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

@QueryProjection
public record PlaceScheduleDto(
        PlaceEntity place,
        ScheduleType scheduleType
) {}
