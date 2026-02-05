package solvit.teachmon.domain.student_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudentScheduleRepository extends JpaRepository<StudentScheduleEntity, Long>, StudentScheduleQueryDslRepository {
    @Query("SELECT s FROM StudentScheduleEntity s JOIN FETCH s.student WHERE s.student IN :students AND s.day = :day AND s.period = :period")
    List<StudentScheduleEntity> findAllByStudentsAndDayAndPeriod(List<StudentEntity> students, LocalDate day, SchoolPeriod period);
}
