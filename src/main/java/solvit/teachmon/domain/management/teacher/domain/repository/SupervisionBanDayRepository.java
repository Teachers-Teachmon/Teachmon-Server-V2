package solvit.teachmon.domain.management.teacher.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDayEntity;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

public interface SupervisionBanDayRepository extends JpaRepository<SupervisionBanDayEntity, Long> {
    @Query("select s.weekDay from SupervisionBanDayEntity s where s.teacher.id = :teacherId")
    List<WeekDay> findWeekDaysByTeacherId(Long teacherId);

    void deleteAllByTeacherId(Long teacherId);
}
