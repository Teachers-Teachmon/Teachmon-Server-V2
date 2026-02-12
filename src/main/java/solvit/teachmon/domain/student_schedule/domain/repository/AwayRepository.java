package solvit.teachmon.domain.student_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.student_schedule.domain.entity.AwayEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AwayRepository extends JpaRepository<AwayEntity, Long> {
    @Query("SELECT a FROM AwayEntity a " +
            "JOIN FETCH a.student " +
            "WHERE a.day >= :baseDate")
    List<AwayEntity> findAllFromDate(@Param("baseDate") LocalDate baseDate);
}
