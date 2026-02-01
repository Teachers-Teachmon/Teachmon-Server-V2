package solvit.teachmon.domain.supervision.domain.dto;

import lombok.Builder;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

/**
 * 하루 감독 배정 결과
 */
@Builder
public record DailySupervisionAssignment(
        TeacherSupervisionInfo selfStudyTeacher,    // 자습 감독 교사
        TeacherSupervisionInfo leaveSeatTeacher     // 이석 감독 교사
) {

    /**
     * 감독 배정 정보를 SupervisionScheduleEntity로 변환
     * 7~11교시까지 각각 자습감독, 이석감독 스케줄 생성
     */
    public List<SupervisionScheduleEntity> toScheduleEntities(LocalDate date, TeacherRepository teacherRepository) {
        // 교사 엔티티 조회
        TeacherEntity selfStudyTeacherEntity = teacherRepository.findById(selfStudyTeacher.teacherId())
                .orElseThrow(TeacherNotFoundException::new);
        TeacherEntity leaveSeatTeacherEntity = teacherRepository.findById(leaveSeatTeacher.teacherId())
                .orElseThrow(TeacherNotFoundException::new);

        // 감독 대상 교시들
        SchoolPeriod[] periods = {
                SchoolPeriod.SEVEN_PERIOD,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                SchoolPeriod.TEN_AND_ELEVEN_PERIOD
        };

        // 각 교시별로 자습감독, 이석감독 스케줄 생성
        return List.of(
                // 자습 감독 스케줄들
                SupervisionScheduleEntity.builder()
                        .teacher(selfStudyTeacherEntity)
                        .day(date)
                        .period(periods[0])
                        .type(SupervisionType.SELF_STUDY_SUPERVISION)
                        .build(),
                SupervisionScheduleEntity.builder()
                        .teacher(selfStudyTeacherEntity)
                        .day(date)
                        .period(periods[1])
                        .type(SupervisionType.SELF_STUDY_SUPERVISION)
                        .build(),
                SupervisionScheduleEntity.builder()
                        .teacher(selfStudyTeacherEntity)
                        .day(date)
                        .period(periods[2])
                        .type(SupervisionType.SELF_STUDY_SUPERVISION)
                        .build(),

                // 이석 감독 스케줄들
                SupervisionScheduleEntity.builder()
                        .teacher(leaveSeatTeacherEntity)
                        .day(date)
                        .period(periods[0])
                        .type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                        .build(),
                SupervisionScheduleEntity.builder()
                        .teacher(leaveSeatTeacherEntity)
                        .day(date)
                        .period(periods[1])
                        .type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                        .build(),
                SupervisionScheduleEntity.builder()
                        .teacher(leaveSeatTeacherEntity)
                        .day(date)
                        .period(periods[2])
                        .type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                        .build()
        );
    }
}