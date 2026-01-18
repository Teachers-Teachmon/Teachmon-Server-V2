package solvit.teachmon.domain.student_schedule.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.entity.QStudentEntity;
import solvit.teachmon.domain.student_schedule.application.dto.QStudentScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.domain.entity.QScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.QStudentScheduleEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class StudentScheduleRepositoryImpl implements StudentScheduleRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Integer, List<StudentScheduleDto>> findByGradeAndPeriodGroupByClass(Integer grade, LocalDate day, SchoolPeriod period) {
        QStudentEntity student = QStudentEntity.studentEntity;
        QStudentScheduleEntity studentSchedule = QStudentScheduleEntity.studentScheduleEntity;
        QScheduleEntity schedule = QScheduleEntity.scheduleEntity;
        QScheduleEntity scheduleSub = new QScheduleEntity("scheduleSub");

        return queryFactory
                .from(student)
                .leftJoin(studentSchedule).on(studentSchedule.student.id.eq(student.id))
                .leftJoin(schedule).on(
                        studentSchedule.id.eq(schedule.studentSchedule.id)
                        // stack_order 가 가장 높은 스케줄 가져오기
                        // stack_order 가 가장 큰 스케줄이 최신 데이터
                        .and(schedule.stackOrder.eq(
                                JPAExpressions
                                        .select(scheduleSub.stackOrder.max())
                                        .from(scheduleSub)
                                        .where(scheduleSub.studentSchedule.id.eq(studentSchedule.id))
                        ))
                )
                .where(
                        gradeEq(grade),
                        dayEq(day),
                        periodEq(period)
                )
                .transform(
                        groupBy(student.classNumber).as(
                                list(
                                        new QStudentScheduleDto(
                                                student.id,
                                                student.grade,
                                                student.classNumber,
                                                student.number,
                                                student.name,
                                                studentSchedule.day,
                                                studentSchedule.period,
                                                studentSchedule.id,
                                                schedule.type
                                        )
                                )
                        )
                );
    }

    private BooleanExpression gradeEq(Integer grade) {
        QStudentEntity student = QStudentEntity.studentEntity;
        return grade != null ? student.grade.eq(grade) : null;
    }

    private BooleanExpression dayEq(LocalDate day) {
        QStudentScheduleEntity studentSchedule = QStudentScheduleEntity.studentScheduleEntity;
        return day != null ? studentSchedule.day.eq(day) : null;
    }

    private BooleanExpression periodEq(SchoolPeriod period) {
        QStudentScheduleEntity studentSchedule = QStudentScheduleEntity.studentScheduleEntity;
        return period != null ? studentSchedule.period.eq(period) : null;
    }
}
