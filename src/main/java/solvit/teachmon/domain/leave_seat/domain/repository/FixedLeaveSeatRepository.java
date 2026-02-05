package solvit.teachmon.domain.leave_seat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FixedLeaveSeatRepository extends JpaRepository<FixedLeaveSeatEntity, Long> {
    @Query("SELECT DISTINCT f FROM FixedLeaveSeatEntity f " +
           "JOIN FETCH f.teacher " +
           "JOIN FETCH f.place")
    List<FixedLeaveSeatEntity> findAllWithFetch();

    @Query("SELECT f FROM FixedLeaveSeatEntity f " +
           "JOIN FETCH f.teacher " +
           "JOIN FETCH f.place " +
           "WHERE f.id = :id")
    Optional<FixedLeaveSeatEntity> findByIdWithFetch(@Param("id") Long id);
}
