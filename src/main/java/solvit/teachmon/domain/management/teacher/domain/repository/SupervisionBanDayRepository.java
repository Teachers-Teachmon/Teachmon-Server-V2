package solvit.teachmon.domain.management.teacher.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDayEntity;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

@Repository
public interface SupervisionBanDayRepository extends JpaRepository<SupervisionBanDayEntity, Long> {
    @Query("select s.weekDay from SupervisionBanDayEntity s where s.teacher.id = :teacherId")
    List<WeekDay> findAllWeekDaysByTeacherId(Long teacherId);

    @Modifying
    @Query("delete from SupervisionBanDayEntity s where s.isAfterschool = true")
    void deleteAllByIsAfterschool();

    @Modifying
    @Query("delete from SupervisionBanDayEntity s where s.teacher.id = :teacherId and s.weekDay = :weekDay and s.isAfterschool = true")
    void deleteAfterSchoolBanDay(@Param("teacherId") Long teacherId, @Param("weekDay") WeekDay weekDay);
}
