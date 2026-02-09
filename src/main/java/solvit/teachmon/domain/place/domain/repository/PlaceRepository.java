package solvit.teachmon.domain.place.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;

import org.springframework.stereotype.Repository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

@Repository
public interface PlaceRepository extends JpaRepository<PlaceEntity, Long>, PlaceQueryDslRepository {
    @Query("""
            SELECT CASE WHEN COUNT(ass) > 0 THEN TRUE ELSE FALSE END
            FROM AdditionalSelfStudyScheduleEntity ass
            JOIN ass.schedule s
            JOIN s.studentSchedule ss
            WHERE ss.day = :day
              AND ss.period = :period
              AND ass.place = :place
            """)
    boolean existByDayAndPeriodAndPlace(
            @Param("day") LocalDate day,
            @Param("period") SchoolPeriod period,
            @Param("place") PlaceEntity place
    );
}
