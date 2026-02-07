package solvit.teachmon.domain.self_study.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdditionalSelfStudyRepository extends JpaRepository<AdditionalSelfStudyEntity, Long>, AdditionalSelfStudyQueryDslRepository {
    @Query("SELECT a FROM AdditionalSelfStudyEntity a WHERE YEAR(a.day) = :year")
    List<AdditionalSelfStudyEntity> findByYear(Integer year);

    @Query("SELECT a FROM AdditionalSelfStudyEntity a WHERE a.day BETWEEN :startDay AND :endDay")
    List<AdditionalSelfStudyEntity> findAllByDayBetween(LocalDate startDay, LocalDate endDay);
}
