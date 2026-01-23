package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;

@Repository
public interface AfterSchoolRepository extends JpaRepository<AfterSchoolEntity, Long> {
}
