package solvit.teachmon.domain.self_study.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;

import java.util.List;

public interface SelfStudyRepository extends JpaRepository<SelfStudyEntity, Long> {
    void deleteAllByBranchAndGrade(BranchEntity branch, Integer grade);

    List<SelfStudyEntity> findAllByBranchAndGrade(BranchEntity branch, Integer grade);
}
