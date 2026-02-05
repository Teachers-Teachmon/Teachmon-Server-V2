package solvit.teachmon.domain.leave_seat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatStudentEntity;

import java.util.List;

@Repository
public interface FixedLeaveSeatStudentRepository extends JpaRepository<FixedLeaveSeatStudentEntity, Long> {
    @Query("SELECT f FROM FixedLeaveSeatStudentEntity f " +
           "JOIN FETCH f.student " +
           "WHERE f.fixedLeaveSeat = :fixedLeaveSeat")
    List<FixedLeaveSeatStudentEntity> findAllByFixedLeaveSeatWithFetch(@Param("fixedLeaveSeat") FixedLeaveSeatEntity fixedLeaveSeat);

    @Modifying
    @Query("DELETE FROM FixedLeaveSeatStudentEntity f WHERE f.fixedLeaveSeat.id = :fixedLeaveSeatId")
    void deleteAllByFixedLeaveSeatId(@Param("fixedLeaveSeatId") Long fixedLeaveSeatId);
}
