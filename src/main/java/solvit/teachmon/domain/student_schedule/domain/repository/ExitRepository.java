package solvit.teachmon.domain.student_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExitRepository extends JpaRepository<ExitEntity, Long> {
    @Query("SELECT e FROM ExitEntity e " +
            "JOIN FETCH e.student " +
            "JOIN FETCH e.teacher " +
            "WHERE e.day BETWEEN :startDate AND :endDate " +
            "ORDER BY e.day DESC, e.period ASC")
    List<ExitEntity> findAllByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM ExitEntity e " +
            "JOIN FETCH e.student " +
            "JOIN FETCH e.teacher " +
            "WHERE e.day = :day " +
            "ORDER BY e.period ASC")
    List<ExitEntity> findAllByDay(@Param("day") LocalDate day);

    @Query("SELECT e FROM ExitEntity e " +
            "JOIN FETCH e.student " +
            "WHERE e.day >= :baseDate")
    List<ExitEntity> findAllFromDate(@Param("baseDate") LocalDate baseDate);

    @Query("SELECT e FROM ExitEntity e " +
            "WHERE e.day = :day AND e.period = :period AND e.student = :student")
    Optional<ExitEntity> findByDayAndPeriodAndStudent(LocalDate day, SchoolPeriod period, StudentEntity student);
}
