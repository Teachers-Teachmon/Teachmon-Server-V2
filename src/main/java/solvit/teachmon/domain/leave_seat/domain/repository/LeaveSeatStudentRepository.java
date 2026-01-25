package solvit.teachmon.domain.leave_seat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatStudentEntity;

@Repository
public interface LeaveSeatStudentRepository extends JpaRepository<LeaveSeatStudentEntity, Long> {
}
