package solvit.teachmon.domain.self_study.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;

import java.util.List;

@Repository
public interface SelfStudyRepository extends JpaRepository<SelfStudyEntity, Long>, SelfStudyRepositoryCustom {
    void deleteAllByBranchAndGrade(BranchEntity branch, Integer grade);

    List<SelfStudyEntity> findAllByBranchAndGrade(BranchEntity branch, Integer grade);
}
