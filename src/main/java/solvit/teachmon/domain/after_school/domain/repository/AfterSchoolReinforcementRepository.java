package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;

import java.time.LocalDate;
import java.util.List;

public interface AfterSchoolReinforcementRepository extends JpaRepository<AfterSchoolReinforcementEntity, Long> {
    @Query("""
        SELECT a
        FROM AfterSchoolReinforcementEntity a
        JOIN FETCH a.afterSchool
        JOIN FETCH a.place
        WHERE a.changeDay BETWEEN :startDay AND :endDay
    """)
    List<AfterSchoolReinforcementEntity> findAllByChangeDayBetween(
            @Param("startDay") LocalDate startDay,
            @Param("endDay") LocalDate endDay
    );
}