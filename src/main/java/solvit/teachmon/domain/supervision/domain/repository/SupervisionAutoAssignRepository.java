package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

/**
 * 감독 자동 배정을 위한 Repository
 */
public interface SupervisionAutoAssignRepository {

    /**
     * Role이 TEACHER인 교사들의 감독 정보 조회
     * 최근 감독일과 총 감독횟수 포함
     */
    @Query("""
        SELECT t.id AS teacherId, 
               t.name AS teacherName,
               MAX(ss.day) AS lastSupervisionDate,
               COUNT(ss.id) AS totalSupervisionCount
        FROM TeacherEntity t
        LEFT JOIN SupervisionScheduleEntity ss ON ss.teacher = t
        WHERE t.role = :role AND t.isActive = true
        GROUP BY t.id, t.name
    """)
    List<TeacherSupervisionInfoProjection> findTeacherSupervisionInfoByRole(@Param("role") Role role);

    /**
     * 교사들의 금지요일 정보 조회
     */
    @Query("""
        SELECT bd.teacher.id AS teacherId, 
               bd.weekDay AS weekDay
        FROM SupervisionBanDayEntity bd
        WHERE bd.teacher.id IN :teacherIds
    """)
    List<SupervisionBanDayProjection> findBanDaysByTeacherIds(@Param("teacherIds") List<Long> teacherIds);

    /**
     * 특정 날짜에 이미 스케줄이 존재하는지 확인
     */
    @Query("""
        SELECT COUNT(ss) > 0
        FROM SupervisionScheduleEntity ss
        WHERE ss.day = :date
    """)
    boolean existsScheduleByDate(@Param("date") LocalDate date);

    /**
     * Projection 인터페이스들
     */
    interface TeacherSupervisionInfoProjection {
        Long getTeacherId();
        String getTeacherName();
        LocalDate getLastSupervisionDate();
        Long getTotalSupervisionCount();
    }

    interface SupervisionBanDayProjection {
        Long getTeacherId();
        WeekDay getWeekDay();
    }
}