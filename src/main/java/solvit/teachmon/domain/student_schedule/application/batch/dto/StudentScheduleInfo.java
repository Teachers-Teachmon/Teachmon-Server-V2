package solvit.teachmon.domain.student_schedule.application.batch.dto;

import solvit.teachmon.global.enums.SchoolPeriod;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * ExecutionContext 직렬화용 경량 StudentSchedule 정보.
 * JPA entity(StudentScheduleEntity) 대신 사용해 Serializable 제약 회피.
 */
public record StudentScheduleInfo(
        long id,
        int grade,
        int classNumber,
        LocalDate day,
        SchoolPeriod period
) implements Serializable {
}
