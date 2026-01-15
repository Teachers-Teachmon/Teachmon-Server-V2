package solvit.teachmon.domain.self_study.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;

import java.util.List;

@Repository
public interface AdditionalSelfStudyRepository extends JpaRepository<AdditionalSelfStudyEntity, Long> {
    @Query("SELECT a FROM AdditionalSelfStudyEntity a WHERE YEAR(a.day) = :year")
    List<AdditionalSelfStudyEntity> findByYear(Integer year);
}
