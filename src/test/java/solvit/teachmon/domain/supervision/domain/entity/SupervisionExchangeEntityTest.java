package solvit.teachmon.domain.supervision.domain.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionExchangeType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

@DisplayName("감독 교체 엔티티 테스트")
class SupervisionExchangeEntityTest {

    private TeacherEntity senderTeacher;
    private TeacherEntity recipientTeacher;
    private SupervisionScheduleEntity senderSchedule;
    private SupervisionScheduleEntity recipientSchedule;

    @BeforeEach
    void setUp() {
        senderTeacher = TeacherEntity.builder()
                .name("송혜정")
                .mail("song@test.com")
                .providerId("1")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        ReflectionTestUtils.setField(senderTeacher, "id", 1L);

        recipientTeacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@test.com")
                .providerId("2")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        ReflectionTestUtils.setField(recipientTeacher, "id", 2L);

        senderSchedule = SupervisionScheduleEntity.builder()
                .teacher(senderTeacher)
                .day(LocalDate.of(2025, 3, 2))
                .period(SchoolPeriod.SEVEN_PERIOD)
                .type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build();

        recipientSchedule = SupervisionScheduleEntity.builder()
                .teacher(recipientTeacher)
                .day(LocalDate.of(2025, 3, 3))
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                .type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build();
    }

    @Test
    @DisplayName("감독 교체 엔티티가 PENDING 상태로 생성된다")
    void shouldCreateExchangeWithPendingStatus() {
        // Given
        String reason = "개인 사유로 교체 요청드립니다";

        // When
        SupervisionExchangeEntity exchange = SupervisionExchangeEntity.builder()
                .sender(senderTeacher)
                .recipient(recipientTeacher)
                .senderSchedule(senderSchedule)
                .recipientSchedule(recipientSchedule)
                .reason(reason)
                .build();

        // Then
        assertThat(exchange.getSender()).isEqualTo(senderTeacher);
        assertThat(exchange.getRecipient()).isEqualTo(recipientTeacher);
        assertThat(exchange.getSenderSchedule()).isEqualTo(senderSchedule);
        assertThat(exchange.getRecipientSchedule()).isEqualTo(recipientSchedule);
        assertThat(exchange.getReason()).isEqualTo(reason);
        assertThat(exchange.getState()).isEqualTo(SupervisionExchangeType.PENDING);
    }

    @Test
    @DisplayName("교체 요청을 수락할 수 있다")
    void shouldAcceptExchangeRequest() {
        // Given
        SupervisionExchangeEntity exchange = SupervisionExchangeEntity.builder()
                .sender(senderTeacher)
                .recipient(recipientTeacher)
                .senderSchedule(senderSchedule)
                .recipientSchedule(recipientSchedule)
                .reason("개인 사유")
                .build();

        // When
        exchange.accept();

        // Then
        assertThat(exchange.getState()).isEqualTo(SupervisionExchangeType.ACCEPTED);
    }

    @Test
    @DisplayName("교체 요청을 거절할 수 있다")
    void shouldRejectExchangeRequest() {
        // Given
        SupervisionExchangeEntity exchange = SupervisionExchangeEntity.builder()
                .sender(senderTeacher)
                .recipient(recipientTeacher)
                .senderSchedule(senderSchedule)
                .recipientSchedule(recipientSchedule)
                .reason("개인 사유")
                .build();

        // When
        exchange.reject();

        // Then
        assertThat(exchange.getState()).isEqualTo(SupervisionExchangeType.REJECTED);
    }
}
