package solvit.teachmon.domain.leave_seat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveSeatRepository extends JpaRepository<LeaveSeatEntity, Long> {
    @Query("SELECT l FROM LeaveSeatEntity l WHERE l.place = :place AND l.day = :day AND l.period = :period")
    Optional<LeaveSeatEntity> findByPlaceAndDayAndPeriod(PlaceEntity place, LocalDate day, SchoolPeriod period);

    @Query("SELECT DISTINCT l FROM LeaveSeatEntity l " +
           "JOIN FETCH l.teacher " +
           "JOIN FETCH l.place " +
           "WHERE l.day = :day AND l.period = :period")
    List<LeaveSeatEntity> findAllByDayAndPeriodWithFetch(@Param("day") LocalDate day, @Param("period") SchoolPeriod period);

    @Query("SELECT l FROM LeaveSeatEntity l " +
           "JOIN FETCH l.teacher " +
           "JOIN FETCH l.place " +
           "WHERE l.id = :id")
    Optional<LeaveSeatEntity> findByIdWithFetch(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN false ELSE true END FROM LeaveSeatEntity l WHERE l.place.id = :placeId AND l.day = :day AND l.period = :period")
    Boolean isPlaceAvailableForLeaveSeat(@Param("placeId") Long placeId, @Param("day") LocalDate day, @Param("period") SchoolPeriod period);

    @Query("SELECT DISTINCT l FROM LeaveSeatEntity l " +
           "JOIN FETCH l.leaveSeatStudents ls " +
           "JOIN FETCH ls.student " +
           "WHERE l.day >= :baseDate")
    List<LeaveSeatEntity> findAllFromDate(@Param("baseDate") LocalDate baseDate);
}
