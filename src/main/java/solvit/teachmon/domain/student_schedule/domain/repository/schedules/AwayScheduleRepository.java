package solvit.teachmon.domain.student_schedule.domain.repository.schedules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AwayScheduleEntity;

@Repository
public interface AwayScheduleRepository extends JpaRepository<AwayScheduleEntity, Long> {
}
