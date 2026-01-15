package solvit.teachmon.domain.self_study.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;

public interface SelfStudyRepository extends JpaRepository<SelfStudyEntity, Long> {
    void deleteAllByYearAndBranchAndGrade(Integer year, BranchEntity branch, Integer grade);
}
