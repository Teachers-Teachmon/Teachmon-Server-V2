package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;

import java.time.LocalDate;

public interface AfterSchoolBusinessTripRepository extends JpaRepository<AfterSchoolBusinessTripEntity, Long> {
    // TODO JPQL 로 바꿔주기
    Boolean existsByAfterSchoolAndDay(AfterSchoolEntity afterSchool, LocalDate day);
}