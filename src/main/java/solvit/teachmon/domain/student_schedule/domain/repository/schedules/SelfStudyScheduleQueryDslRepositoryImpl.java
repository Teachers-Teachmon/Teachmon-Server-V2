package solvit.teachmon.domain.student_schedule.domain.repository.schedules;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.entity.QStudentEntity;
import solvit.teachmon.domain.place.domain.entity.QPlaceEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.QPlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.QStudentScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.domain.entity.QScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.QStudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.QSelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class SelfStudyScheduleQueryDslRepositoryImpl implements SelfStudyScheduleQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Integer, Long> getSelfStudyPlaceCount(List<ScheduleEntity> schedules) {
        QPlaceEntity place = QPlaceEntity.placeEntity;
        QSelfStudyScheduleEntity selfStudySchedule = QSelfStudyScheduleEntity.selfStudyScheduleEntity;

        return queryFactory
                .from(selfStudySchedule)
                .join(selfStudySchedule.place, place)
                .where(selfStudySchedule.schedule.in(schedules))
                .groupBy(place.floor)
                .transform(
                        groupBy(place.floor)
                                .as(selfStudySchedule.place.id.countDistinct())
                );
    }

    @Override
    public List<PlaceScheduleDto> getPlaceScheduleByFloor(List<ScheduleEntity> schedules, Integer floor) {
        QPlaceEntity place = QPlaceEntity.placeEntity;
        QSelfStudyScheduleEntity selfStudySchedule = QSelfStudyScheduleEntity.selfStudyScheduleEntity;

        return queryFactory
                .selectDistinct(new QPlaceScheduleDto(
                        place,
                        Expressions.constant(ScheduleType.SELF_STUDY)
                ))
                .from(selfStudySchedule)
                .join(selfStudySchedule.place, place)
                .where(
                        selfStudySchedule.schedule.in(schedules),
                        place.floor.eq(floor)
                )
                .fetch();
    }

    @Override
    public List<StudentScheduleDto> getStudentScheduleByPlaceAndDayAndPeriod(Long placeId, LocalDate day, SchoolPeriod period) {
        QSelfStudyScheduleEntity selfStudySchedule = QSelfStudyScheduleEntity.selfStudyScheduleEntity;
        QScheduleEntity schedule = QScheduleEntity.scheduleEntity;
        QStudentScheduleEntity studentSchedule = QStudentScheduleEntity.studentScheduleEntity;
        QStudentEntity student = QStudentEntity.studentEntity;
        QScheduleEntity scheduleSub = new QScheduleEntity("scheduleSub");
        QScheduleEntity scheduleMax = new QScheduleEntity("scheduleMax");
        QScheduleEntity schedulePlaceBased = new QScheduleEntity("schedulePlaceBased");

        return queryFactory
                .select(new QStudentScheduleDto(
                        student.id,
                        student.grade,
                        student.classNumber,
                        student.number,
                        student.name,
                        studentSchedule.day,
                        studentSchedule.period,
                        studentSchedule.id,
                        scheduleMax.type
                ))
                .from(selfStudySchedule)
                .join(schedule).on(selfStudySchedule.schedule.id.eq(schedule.id))
                .join(schedule.studentSchedule, studentSchedule)
                .join(studentSchedule.student, student)
                // 최신 스케줄 조인 (EXIT/AWAY 정보 표시용)
                .join(scheduleMax).on(
                        scheduleMax.studentSchedule.id.eq(studentSchedule.id)
                                .and(Expressions.list(scheduleMax.studentSchedule.id, scheduleMax.stackOrder).in(
                                        JPAExpressions
                                                .select(scheduleSub.studentSchedule.id, scheduleSub.stackOrder.max())
                                                .from(scheduleSub)
                                                .groupBy(scheduleSub.studentSchedule.id)
                                ))
                )
                .where(
                        selfStudySchedule.place.id.eq(placeId),
                        studentSchedule.day.eq(day),
                        studentSchedule.period.eq(period),
                        // 케이스 1: 최신 스케줄이 자습인 경우
                        schedule.id.eq(scheduleMax.id)
                                // 케이스 2: 최신 스케줄이 EXIT/AWAY이고, 현재 스케줄이 EXIT/AWAY를 제외한 가장 최근 스케줄인 경우
                                .or(
                                        scheduleMax.type.in(ScheduleType.EXIT, ScheduleType.AWAY)
                                                .and(schedule.stackOrder.eq(
                                                        JPAExpressions
                                                                .select(schedulePlaceBased.stackOrder.max())
                                                                .from(schedulePlaceBased)
                                                                .where(
                                                                        schedulePlaceBased.studentSchedule.id.eq(studentSchedule.id),
                                                                        schedulePlaceBased.type.notIn(ScheduleType.EXIT, ScheduleType.AWAY)
                                                                )
                                                ))
                                )
                )
                .fetch();
    }
}
