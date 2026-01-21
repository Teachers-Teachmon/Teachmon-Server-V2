package solvit.teachmon.domain.student_schedule.domain.repository.schedules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.student_schedule.domain.entity.AwayEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AwayScheduleEntity;

import java.util.Optional;

@Repository
public interface AwayScheduleRepository extends JpaRepository<AwayScheduleEntity, Long> {
    @Query("SELECT ase FROM AwayScheduleEntity ase WHERE ase.schedule.id = :scheduleId")
    Optional<AwayScheduleEntity> findByScheduleId(Long scheduleId);
}
