package solvit.teachmon.domain.student_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.AwayEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AwayRepository extends JpaRepository<AwayEntity, Long> {
    @Query("SELECT a FROM AwayEntity a " +
            "JOIN FETCH a.student " +
            "WHERE a.day >= :baseDate")
    List<AwayEntity> findAllFromDate(@Param("baseDate") LocalDate baseDate);

    @Query("SELECT a FROM AwayEntity a " +
            "WHERE a.day = :day AND a.period = :period AND a.student = :student")
    Optional<AwayEntity> findByDayAndPeriodAndStudent(LocalDate day, SchoolPeriod period, StudentEntity student);
}
