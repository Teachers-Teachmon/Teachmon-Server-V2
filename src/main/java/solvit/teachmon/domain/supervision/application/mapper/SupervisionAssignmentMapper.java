package solvit.teachmon.domain.supervision.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.supervision.domain.vo.DailySupervisionAssignment;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SupervisionAssignmentMapper {
    
    default List<SupervisionScheduleEntity> toScheduleEntities(
            DailySupervisionAssignment assignment, 
            LocalDate date,
            TeacherEntity selfStudyTeacherEntity,
            TeacherEntity leaveSeatTeacherEntity) {
        
        SchoolPeriod[] periods = {
                SchoolPeriod.SEVEN_PERIOD,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                SchoolPeriod.TEN_AND_ELEVEN_PERIOD
        };

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