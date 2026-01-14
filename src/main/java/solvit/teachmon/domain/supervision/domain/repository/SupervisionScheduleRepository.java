package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;

public interface SupervisionScheduleRepository extends JpaRepository<SupervisionScheduleEntity, Long>, SupervisionScheduleRepositoryCustom {
}
