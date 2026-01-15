package solvit.teachmon.domain.self_study.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;

@Repository
public interface AdditionalSelfStudyRepository extends JpaRepository<AdditionalSelfStudyEntity, Long> {
}
