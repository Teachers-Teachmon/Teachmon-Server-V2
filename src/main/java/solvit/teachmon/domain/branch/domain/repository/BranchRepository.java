package solvit.teachmon.domain.branch.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    Optional<BranchEntity> findByYearAndBranch(Integer year, Integer branch);
}
