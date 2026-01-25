package solvit.teachmon.domain.leave_seat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatStudentEntity;

@Repository
public interface FixedLeaveSeatStudentRepository extends JpaRepository<FixedLeaveSeatStudentEntity, Long> {
}
