package solvit.teachmon.domain.student_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.student_schedule.domain.entity.AwayEntity;

@Repository
public interface AwayRepository extends JpaRepository<AwayEntity, Long> {
}
