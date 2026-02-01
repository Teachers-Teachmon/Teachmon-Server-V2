package solvit.teachmon.domain.supervision.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionExchangeType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.entity.BaseEntity;

@Entity
@Getter
@Table(name = "supervision_exchange")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupervisionExchangeEntity extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private TeacherEntity sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private TeacherEntity recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_schedule_id", nullable = false)
    private SupervisionScheduleEntity senderSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_schedule_id", nullable = false)
    private SupervisionScheduleEntity recipientSchedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private SupervisionExchangeType state;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Builder
    public SupervisionExchangeEntity(TeacherEntity sender, TeacherEntity recipient, 
                                   SupervisionScheduleEntity senderSchedule, 
                                   SupervisionScheduleEntity recipientSchedule, 
                                   String reason) {
        this.sender = sender;
        this.recipient = recipient;
        this.senderSchedule = senderSchedule;
        this.recipientSchedule = recipientSchedule;
        this.reason = reason;
        this.state = SupervisionExchangeType.PENDING;
    }

    public void accept() {
        this.state = SupervisionExchangeType.ACCEPTED;
    }

    public void reject() {
        this.state = SupervisionExchangeType.REJECTED;
    }
}
