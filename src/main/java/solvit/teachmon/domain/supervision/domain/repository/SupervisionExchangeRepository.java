package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;

public interface SupervisionExchangeRepository extends JpaRepository<SupervisionExchangeEntity, Long> {
}