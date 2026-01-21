package solvit.teachmon.domain.student_schedule.domain.repository.schedules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.ExitScheduleEntity;

import java.util.Optional;

@Repository
public interface ExitScheduleRepository extends JpaRepository<ExitScheduleEntity, Long> {
    @Query("SELECT es FROM ExitScheduleEntity es WHERE es.schedule.id = :scheduleId")
    Optional<ExitScheduleEntity> findByScheduleId(Long scheduleId);
}
