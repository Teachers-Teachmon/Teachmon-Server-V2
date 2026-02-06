package solvit.teachmon.domain.supervision.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionExchangeType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("감독 교체 저장소 테스트")
class SupervisionExchangeRepositoryTest {

    @Autowired
    private SupervisionExchangeRepository supervisionExchangeRepository;

    @Autowired
    private SupervisionScheduleRepository supervisionScheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

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
        teacherRepository.save(senderTeacher);

        recipientTeacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@test.com")
                .providerId("2")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        teacherRepository.save(recipientTeacher);

        senderSchedule = SupervisionScheduleEntity.builder()
                .teacher(senderTeacher)
                .day(LocalDate.of(2025, 3, 2))
                .period(SchoolPeriod.SEVEN_PERIOD)
                .type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build();
        supervisionScheduleRepository.save(senderSchedule);

        recipientSchedule = SupervisionScheduleEntity.builder()
                .teacher(recipientTeacher)
                .day(LocalDate.of(2025, 3, 3))
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                .type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build();
        supervisionScheduleRepository.save(recipientSchedule);
    }

    @Test
    @DisplayName("감독 교체 요청을 저장할 수 있다")
    void shouldSaveSupervisionExchange() {
        // Given
        SupervisionExchangeEntity exchange = SupervisionExchangeEntity.builder()
                .sender(senderTeacher)
                .recipient(recipientTeacher)
                .senderSchedule(senderSchedule)
                .recipientSchedule(recipientSchedule)
                .reason("개인 사유로 교체 요청드립니다")
                .build();

        // When
        SupervisionExchangeEntity savedExchange = supervisionExchangeRepository.save(exchange);

        // Then
        assertThat(savedExchange.getId()).isNotNull();
        assertThat(savedExchange.getSender().getName()).isEqualTo("송혜정");
        assertThat(savedExchange.getRecipient().getName()).isEqualTo("김선생");
        assertThat(savedExchange.getReason()).isEqualTo("개인 사유로 교체 요청드립니다");
        assertThat(savedExchange.getState()).isEqualTo(SupervisionExchangeType.PENDING);
    }

    @Test
    @DisplayName("ID로 감독 교체 요청을 찾을 수 있다")
    void shouldFindSupervisionExchangeById() {
        // Given
        SupervisionExchangeEntity exchange = SupervisionExchangeEntity.builder()
                .sender(senderTeacher)
                .recipient(recipientTeacher)
                .senderSchedule(senderSchedule)
                .recipientSchedule(recipientSchedule)
                .reason("개인 사유")
                .build();
        SupervisionExchangeEntity savedExchange = supervisionExchangeRepository.save(exchange);

        // When
        Optional<SupervisionExchangeEntity> result = supervisionExchangeRepository.findById(savedExchange.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSender().getName()).isEqualTo("송혜정");
        assertThat(result.get().getRecipient().getName()).isEqualTo("김선생");
        assertThat(result.get().getReason()).isEqualTo("개인 사유");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 찾으면 빈 결과가 반환된다")
    void shouldReturnEmptyWhenExchangeNotExists() {
        // Given
        Long nonExistentId = 999L;

        // When
        Optional<SupervisionExchangeEntity> result = supervisionExchangeRepository.findById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("모든 감독 교체 요청을 조회할 수 있다")
    void shouldFindAllSupervisionExchanges() {
        // Given
        SupervisionExchangeEntity exchange1 = SupervisionExchangeEntity.builder()
                .sender(senderTeacher)
                .recipient(recipientTeacher)
                .senderSchedule(senderSchedule)
                .recipientSchedule(recipientSchedule)
                .reason("첫 번째 요청")
                .build();

        SupervisionExchangeEntity exchange2 = SupervisionExchangeEntity.builder()
                .sender(recipientTeacher)
                .recipient(senderTeacher)
                .senderSchedule(recipientSchedule)
                .recipientSchedule(senderSchedule)
                .reason("두 번째 요청")
                .build();

        supervisionExchangeRepository.save(exchange1);
        supervisionExchangeRepository.save(exchange2);

        // When
        List<SupervisionExchangeEntity> result = supervisionExchangeRepository.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("reason")
                .containsExactlyInAnyOrder("첫 번째 요청", "두 번째 요청");
    }

    @Test
    @DisplayName("감독 교체 요청의 상태를 변경할 수 있다")
    void shouldUpdateSupervisionExchangeState() {
        // Given
        SupervisionExchangeEntity exchange = SupervisionExchangeEntity.builder()
                .sender(senderTeacher)
                .recipient(recipientTeacher)
                .senderSchedule(senderSchedule)
                .recipientSchedule(recipientSchedule)
                .reason("개인 사유")
                .build();
        SupervisionExchangeEntity savedExchange = supervisionExchangeRepository.save(exchange);

        // When
        savedExchange.accept();
        supervisionExchangeRepository.save(savedExchange);

        // Then
        Optional<SupervisionExchangeEntity> result = supervisionExchangeRepository.findById(savedExchange.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getState()).isEqualTo(SupervisionExchangeType.ACCEPTED);
    }
}