package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;

import java.util.List;

@Repository
public interface AfterSchoolRepository extends JpaRepository<AfterSchoolEntity, Long> {
    @Query("SELECT a FROM AfterSchoolEntity a WHERE a.branch = :branch")
    List<AfterSchoolEntity> findAllByBranch(BranchEntity branch);
}
