package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;

import java.time.LocalDate;

public interface AfterSchoolBusinessTripRepository extends JpaRepository<AfterSchoolBusinessTripEntity, Long> {
    @Query("select case when (count(b) > 0) then true else false end " +
            "from AfterSchoolBusinessTripEntity b " +
            "where b.afterSchool = :afterSchool and b.day = :day")
    Boolean existsByAfterSchoolAndDay(AfterSchoolEntity afterSchool, LocalDate day);
}