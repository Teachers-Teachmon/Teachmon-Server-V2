package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;

public interface AfterSchoolBusinessTripRepository extends JpaRepository<AfterSchoolBusinessTripEntity, Long> {
}