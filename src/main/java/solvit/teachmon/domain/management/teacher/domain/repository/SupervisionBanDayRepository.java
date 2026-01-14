package solvit.teachmon.domain.management.teacher.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDay;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

public interface SupervisionBanDayRepository extends JpaRepository<SupervisionBanDay, Long> {
    List<WeekDay> findWeekDaysByTeacherId(Long teacherId);
}
