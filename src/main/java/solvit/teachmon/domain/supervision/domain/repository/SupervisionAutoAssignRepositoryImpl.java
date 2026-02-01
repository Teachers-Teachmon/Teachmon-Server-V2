package solvit.teachmon.domain.supervision.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.teacher.domain.entity.QSupervisionBanDayEntity;
import solvit.teachmon.domain.supervision.domain.entity.QSupervisionScheduleEntity;
import solvit.teachmon.domain.user.domain.entity.QTeacherEntity;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

/**
 * 감독 자동 배정을 위한 Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class SupervisionAutoAssignRepositoryImpl implements SupervisionAutoAssignRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    @SuppressWarnings("unchecked")
    public List<TeacherSupervisionInfoProjection> findTeacherSupervisionInfoByRole(Role role) {
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;
        QSupervisionScheduleEntity schedule = QSupervisionScheduleEntity.supervisionScheduleEntity;

        List<TeacherSupervisionInfoProjectionImpl> results = queryFactory
                .select(Projections.bean(
                        TeacherSupervisionInfoProjectionImpl.class,
                        teacher.id.as("teacherId"),
                        teacher.name.as("teacherName"),
                        schedule.day.max().as("lastSupervisionDate"),
                        schedule.id.count().as("totalSupervisionCount")
                ))
                .from(teacher)
                .leftJoin(schedule).on(schedule.teacher.eq(teacher))
                .where(teacher.role.eq(role).and(teacher.isActive.eq(true)))
                .groupBy(teacher.id, teacher.name)
                .fetch();
        
        return (List<TeacherSupervisionInfoProjection>) (List<?>) results;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SupervisionBanDayProjection> findBanDaysByTeacherIds(List<Long> teacherIds) {
        QSupervisionBanDayEntity banDay = QSupervisionBanDayEntity.supervisionBanDayEntity;

        List<SupervisionBanDayProjectionImpl> results = queryFactory
                .select(Projections.bean(
                        SupervisionBanDayProjectionImpl.class,
                        banDay.teacher.id.as("teacherId"),
                        banDay.weekDay.as("weekDay")
                ))
                .from(banDay)
                .where(banDay.teacher.id.in(teacherIds))
                .fetch();
        
        return (List<SupervisionBanDayProjection>) (List<?>) results;
    }

    @Override
    public boolean existsScheduleByDate(LocalDate date) {
        QSupervisionScheduleEntity schedule = QSupervisionScheduleEntity.supervisionScheduleEntity;

        Integer count = queryFactory
                .selectOne()
                .from(schedule)
                .where(schedule.day.eq(date))
                .fetchFirst();

        return count != null;
    }

    /**
     * Projection 구현체들
     */
    public static class TeacherSupervisionInfoProjectionImpl implements TeacherSupervisionInfoProjection {
        private Long teacherId;
        private String teacherName;
        private LocalDate lastSupervisionDate;
        private Long totalSupervisionCount;

        public TeacherSupervisionInfoProjectionImpl() {}

        @Override
        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        @Override
        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        @Override
        public LocalDate getLastSupervisionDate() {
            return lastSupervisionDate;
        }

        public void setLastSupervisionDate(LocalDate lastSupervisionDate) {
            this.lastSupervisionDate = lastSupervisionDate;
        }

        @Override
        public Long getTotalSupervisionCount() {
            return totalSupervisionCount;
        }

        public void setTotalSupervisionCount(Long totalSupervisionCount) {
            this.totalSupervisionCount = totalSupervisionCount;
        }
    }

    public static class SupervisionBanDayProjectionImpl implements SupervisionBanDayProjection {
        private Long teacherId;
        private WeekDay weekDay;

        public SupervisionBanDayProjectionImpl() {}

        @Override
        public Long getTeacherId() {
            return teacherId;
        }

        public void setTeacherId(Long teacherId) {
            this.teacherId = teacherId;
        }

        @Override
        public WeekDay getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(WeekDay weekDay) {
            this.weekDay = weekDay;
        }
    }
}