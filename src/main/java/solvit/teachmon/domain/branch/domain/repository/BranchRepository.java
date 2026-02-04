package solvit.teachmon.domain.branch.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<BranchEntity, Long> {
    @Query("SELECT b FROM BranchEntity b WHERE b.year = :year AND b.branch = :branch")
    Optional<BranchEntity> findByYearAndBranch(Integer year, Integer branch);

    @Query("""
    select b
    from BranchEntity b
    where :today between b.startDay and b.endDay
""")
    Optional<BranchEntity> findByDay(@Param("today") LocalDate today);

}
