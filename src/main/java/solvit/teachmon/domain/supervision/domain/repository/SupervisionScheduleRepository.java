package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;

import java.time.LocalDate;
import java.util.List;

public interface SupervisionScheduleRepository extends JpaRepository<SupervisionScheduleEntity, Long>, SupervisionScheduleQueryDslRepository {
    void deleteByDay(LocalDate day);
    void deleteByDayAndType(LocalDate day, SupervisionType type);
    
    @Query("SELECT DISTINCT s.type FROM SupervisionScheduleEntity s WHERE s.teacher.id = :teacherId AND s.day = :day")
    List<SupervisionType> findTodaySupervisionTypesByTeacher(@Param("teacherId") Long teacherId, @Param("day") LocalDate day);
    
    @Query("SELECT DISTINCT s.day FROM SupervisionScheduleEntity s WHERE s.teacher.id = :teacherId AND MONTH(s.day) = :month ORDER BY s.day")
    List<LocalDate> findSupervisionDaysByTeacherAndMonth(@Param("teacherId") Long teacherId, @Param("month") Integer month);
}
