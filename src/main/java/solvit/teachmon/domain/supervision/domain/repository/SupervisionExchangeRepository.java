package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;

import java.util.List;

public interface SupervisionExchangeRepository extends JpaRepository<SupervisionExchangeEntity, Long> {
    @Query("SELECT e FROM SupervisionExchangeEntity e WHERE (e.recipient.id = :userId OR e.sender.id = :userId) AND e.state != 'CHECKED'")
    List<SupervisionExchangeEntity> findByRecipientIdOrSenderIdAndExchangeTypeNotChecked(@Param("userId") Long userId);
}