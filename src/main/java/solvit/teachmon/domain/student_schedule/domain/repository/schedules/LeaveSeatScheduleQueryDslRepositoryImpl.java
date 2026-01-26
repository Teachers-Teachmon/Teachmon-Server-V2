package solvit.teachmon.domain.student_schedule.domain.repository.schedules;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.leave_seat.domain.entity.QLeaveSeatEntity;
import solvit.teachmon.domain.management.student.domain.entity.QStudentEntity;
import solvit.teachmon.domain.place.domain.entity.QPlaceEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.QPlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.QStudentScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.domain.entity.QScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.QStudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.QLeaveSeatScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;

@Repository
@RequiredArgsConstructor
public class LeaveSeatScheduleQueryDslRepositoryImpl implements LeaveSeatScheduleQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Integer, Long> getLeaveSeatPlaceCount(List<ScheduleEntity> schedules) {
        QLeaveSeatScheduleEntity leaveSeatSchedule = QLeaveSeatScheduleEntity.leaveSeatScheduleEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;
        return queryFactory
                .from(leaveSeatSchedule)
                .join(leaveSeatSchedule.leaveSeat.place, place)
                .where(leaveSeatSchedule.schedule.in(schedules))
                .groupBy(place.floor)
                .transform(
                        groupBy(place.floor)
                                .as(place.id.countDistinct())
                );
    }

    @Override
    public List<PlaceScheduleDto> getPlaceScheduleByFloor(List<ScheduleEntity> schedules, Integer floor) {
        QPlaceEntity place = QPlaceEntity.placeEntity;
        QLeaveSeatScheduleEntity leaveSeatSchedule = QLeaveSeatScheduleEntity.leaveSeatScheduleEntity;

        return queryFactory
                .selectDistinct(new QPlaceScheduleDto(
                        place,
                        Expressions.constant(ScheduleType.LEAVE_SEAT)
                ))
                .from(leaveSeatSchedule)
                .join(leaveSeatSchedule.leaveSeat.place, place)
                .where(
                        leaveSeatSchedule.schedule.in(schedules),
                        place.floor.eq(floor)
                )
                .fetch();
    }

    @Override
    public List<StudentScheduleDto> getStudentScheduleByPlaceAndDayAndPeriod(Long placeId, LocalDate day, SchoolPeriod period) {
        QLeaveSeatScheduleEntity leaveSeatSchedule = QLeaveSeatScheduleEntity.leaveSeatScheduleEntity;
        QLeaveSeatEntity leaveSeat = QLeaveSeatEntity.leaveSeatEntity;
        QScheduleEntity schedule = QScheduleEntity.scheduleEntity;
        QStudentScheduleEntity studentSchedule = QStudentScheduleEntity.studentScheduleEntity;
        QStudentEntity student = QStudentEntity.studentEntity;
        QScheduleEntity scheduleSub = new QScheduleEntity("scheduleSub");

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
                        schedule.type
                ))
                .from(leaveSeat)
                .join(leaveSeatSchedule).on(leaveSeatSchedule.leaveSeat.id.eq(leaveSeat.id))
                .join(schedule).on(
                        leaveSeatSchedule.schedule.id.eq(schedule.id)
                                // stack_order 가 가장 높은 스케줄 가져오기
                                // 최적화: 상관 서브쿼리를 비상관 서브쿼리로 변경
                                .and(Expressions.list(schedule.studentSchedule.id, schedule.stackOrder).in(
                                        JPAExpressions
                                                .select(scheduleSub.studentSchedule.id, scheduleSub.stackOrder.max())
                                                .from(scheduleSub)
                                                .groupBy(scheduleSub.studentSchedule.id)
                                ))
                )
                .join(schedule.studentSchedule, studentSchedule)
                .join(studentSchedule.student, student)
                .where(
                        leaveSeat.place.id.eq(placeId),
                        studentSchedule.day.eq(day),
                        studentSchedule.period.eq(period)
                )
                .fetch();
    }
}
