package solvit.teachmon.domain.student_schedule.application.batch.dto;

import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.SelfStudyScheduleEntity;

public record SelfStudyScheduleDto(
        ScheduleEntity schedule,
        SelfStudyScheduleEntity selfStudySchedule
) {
}
