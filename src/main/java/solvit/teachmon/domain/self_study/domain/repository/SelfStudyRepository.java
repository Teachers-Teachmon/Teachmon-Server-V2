package solvit.teachmon.domain.self_study.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;

import java.util.List;

@Repository
public interface SelfStudyRepository extends JpaRepository<SelfStudyEntity, Long>, SelfStudyQueryDslRepository {
    @Modifying
    @Query("delete from SelfStudyEntity s where s.branch = :branch and s.grade = :grade")
    void deleteAllByBranchAndGrade(
            @Param("branch") BranchEntity branch,
            @Param("grade") Integer grade
    );

    @Query("SELECT s FROM SelfStudyEntity s WHERE s.branch = :branch")
    List<SelfStudyEntity> findAllByBranch(BranchEntity branch);
}
