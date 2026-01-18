package solvit.teachmon.domain.student_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    @Query("SELECT COALESCE(MAX(s.stackOrder), 0) FROM ScheduleEntity s WHERE s.studentSchedule.id = :studentScheduleId")
    Integer findLastStackOrderByStudentScheduleId(Long studentScheduleId);
}
