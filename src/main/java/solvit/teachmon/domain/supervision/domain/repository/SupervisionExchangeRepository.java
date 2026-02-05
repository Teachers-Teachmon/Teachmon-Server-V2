package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;

import java.util.List;

public interface SupervisionExchangeRepository extends JpaRepository<SupervisionExchangeEntity, Long> {
    List<SupervisionExchangeEntity> findByRecipientId(Long recipientId);
}