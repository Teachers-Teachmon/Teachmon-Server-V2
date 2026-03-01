package solvit.teachmon.domain.supervision.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;

import java.time.LocalDate;
import java.util.List;

public interface SupervisionExchangeRepository extends JpaRepository<SupervisionExchangeEntity, Long> {
    @Query("SELECT e FROM SupervisionExchangeEntity e WHERE (e.recipient.id = :userId OR e.sender.id = :userId) AND e.state != 'CHECKED'")
    List<SupervisionExchangeEntity> findByRecipientIdOrSenderIdAndExchangeTypeNotChecked(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM SupervisionExchangeEntity se WHERE se.senderSchedule.day = :day OR se.recipientSchedule.day = :day")
    void deleteExchangeByDay(@Param("day") LocalDate day);

    @Modifying
    @Query("DELETE FROM SupervisionExchangeEntity se WHERE (se.senderSchedule.day = :day OR se.recipientSchedule.day = :day) AND (se.senderSchedule.type = :type OR se.recipientSchedule.type = :type)")
    void deleteExchangeByDayAndType(@Param("day") LocalDate day, @Param("type") SupervisionType type);
}