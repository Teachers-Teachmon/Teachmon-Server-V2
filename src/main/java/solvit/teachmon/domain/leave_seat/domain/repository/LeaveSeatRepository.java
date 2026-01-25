package solvit.teachmon.domain.leave_seat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LeaveSeatRepository extends JpaRepository<LeaveSeatEntity, Long> {
    @Query("SELECT l FROM LeaveSeatEntity l WHERE l.place = :place AND l.day = :day AND l.period = :period")
    Optional<LeaveSeatEntity> findByPlaceAndDayAndPeriod(PlaceEntity place, LocalDate day, SchoolPeriod period);
}
