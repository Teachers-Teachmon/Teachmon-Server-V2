package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;

import java.time.LocalDate;

public interface SupervisionScheduleRepository extends JpaRepository<SupervisionScheduleEntity, Long>, SupervisionScheduleQueryDslRepository {
    void deleteByDay(LocalDate day);
    void deleteByDayAndType(LocalDate day, SupervisionType type);
}
