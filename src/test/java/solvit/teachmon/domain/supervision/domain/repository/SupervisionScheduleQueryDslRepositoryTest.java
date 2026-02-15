package solvit.teachmon.domain.supervision.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionSortOrder;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionRankResponseDto;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("감독 스케줄 조회 저장소 테스트")
class SupervisionScheduleQueryDslRepositoryTest {

    @Autowired
    private SupervisionScheduleQueryDslRepositoryImpl supervisionScheduleQueryDslRepository;

    @Autowired
    private SupervisionScheduleRepository scheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    private TeacherEntity teacher1;
    private TeacherEntity teacher2;

    @BeforeEach
    void setUp() {
        teacher1 = createAndSaveTeacher("김선생", "kim@test.com");
        teacher2 = createAndSaveTeacher("이선생", "lee@test.com");
        createAndSaveTeacher("박선생", "park@test.com"); // 감독 이력 없는 선생님 (테스트용)
        
        createTestSupervisionSchedules();
    }

    @Test
    @DisplayName("감독 랭킹을 내림차순으로 조회할 수 있다")
    void shouldFindSupervisionRankingsInDescendingOrder() {
        // When: 내림차순 랭킹 조회
        List<SupervisionRankResponseDto> rankings = 
            supervisionScheduleQueryDslRepository.findSupervisionRankings(null, SupervisionSortOrder.DESC);

        // Then: 총 감독 횟수 내림차순으로 정렬됨
        assertThat(rankings).hasSize(3);
        
        // 김선생 (총 3일: 자습2일 + 이석1일 + 7교시1일)
        SupervisionRankResponseDto first = rankings.getFirst();
        assertThat(first.rank()).isEqualTo(1);
        assertThat(first.name()).isEqualTo("김선생");
        assertThat(first.selfStudySupervisionCount()).isEqualTo(2);
        assertThat(first.leaveSeatSupervisionCount()).isEqualTo(1);
        assertThat(first.seventhPeriodSupervisionCount()).isEqualTo(1);
        assertThat(first.totalSupervisionCount()).isEqualTo(4);
        
        // 이선생 (총 2일: 자습1일 + 이석1일)
        SupervisionRankResponseDto second = rankings.get(1);
        assertThat(second.rank()).isEqualTo(2);
        assertThat(second.name()).isEqualTo("이선생");
        assertThat(second.selfStudySupervisionCount()).isEqualTo(1);
        assertThat(second.leaveSeatSupervisionCount()).isEqualTo(1);
        assertThat(second.seventhPeriodSupervisionCount()).isEqualTo(0);
        assertThat(second.totalSupervisionCount()).isEqualTo(2);
        
        // 박선생 (총 0일)
        SupervisionRankResponseDto third = rankings.get(2);
        assertThat(third.rank()).isEqualTo(3);
        assertThat(third.name()).isEqualTo("박선생");
        assertThat(third.selfStudySupervisionCount()).isEqualTo(0);
        assertThat(third.leaveSeatSupervisionCount()).isEqualTo(0);
        assertThat(third.seventhPeriodSupervisionCount()).isEqualTo(0);
        assertThat(third.totalSupervisionCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("감독 랭킹을 오름차순으로 조회할 수 있다")
    void shouldFindSupervisionRankingsInAscendingOrder() {
        // When: 오름차순 랭킹 조회
        List<SupervisionRankResponseDto> rankings = 
            supervisionScheduleQueryDslRepository.findSupervisionRankings(null, SupervisionSortOrder.ASC);

        // Then: 총 감독 횟수 오름차순으로 정렬됨
        assertThat(rankings).hasSize(3);
        
        // 박선생이 첫 번째 (총 0일)
        SupervisionRankResponseDto first = rankings.getFirst();
        assertThat(first.rank()).isEqualTo(1);
        assertThat(first.name()).isEqualTo("박선생");
        assertThat(first.totalSupervisionCount()).isEqualTo(0);
        
        // 이선생이 두 번째 (총 2일)
        SupervisionRankResponseDto second = rankings.get(1);
        assertThat(second.rank()).isEqualTo(2);
        assertThat(second.name()).isEqualTo("이선생");
        assertThat(second.totalSupervisionCount()).isEqualTo(2);
        
        // 김선생이 마지막 (총 4일)
        SupervisionRankResponseDto third = rankings.get(2);
        assertThat(third.rank()).isEqualTo(3);
        assertThat(third.name()).isEqualTo("김선생");
        assertThat(third.totalSupervisionCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("선생님 이름으로 감독 랭킹을 필터링할 수 있다")
    void shouldFilterSupervisionRankingsByTeacherName() {
        // When: 김선생으로 필터링
        List<SupervisionRankResponseDto> rankings = 
            supervisionScheduleQueryDslRepository.findSupervisionRankings("김", SupervisionSortOrder.DESC);

        // Then: 김선생만 조회됨
        assertThat(rankings).hasSize(1);
        
        SupervisionRankResponseDto result = rankings.getFirst();
        assertThat(result.rank()).isEqualTo(1);
        assertThat(result.name()).isEqualTo("김선생");
        assertThat(result.totalSupervisionCount()).isEqualTo(4);
    }

    @Test
    @DisplayName("동일한 날짜에 여러 시간대 감독을 해도 1일로 카운트된다")
    void shouldCountMultiplePeriodsInSameDayAsOneDay() {
        // Given: 김선생이 2025-01-01에 여러 시간대 자습감독 (현재 3개 시간대 있음)
        // (이미 setUp()에서 생성됨)

        // When: 랭킹 조회
        List<SupervisionRankResponseDto> rankings = 
            supervisionScheduleQueryDslRepository.findSupervisionRankings("김", SupervisionSortOrder.DESC);

        // Then: 2025-01-01의 3개 시간대가 1일로 카운트됨
        SupervisionRankResponseDto kimResult = rankings.getFirst();
        assertThat(kimResult.selfStudySupervisionCount()).isEqualTo(2); // 2일간 자습감독
    }

    @Test
    @DisplayName("7교시 감독 횟수가 별도로 집계된다")
    void shouldCountSeventhPeriodSupervisionSeparately() {
        // When: 랭킹 조회
        List<SupervisionRankResponseDto> rankings = 
            supervisionScheduleQueryDslRepository.findSupervisionRankings(null, SupervisionSortOrder.DESC);

        // Then: 김선생의 7교시 감독 횟수가 별도로 집계됨
        SupervisionRankResponseDto kimResult = rankings.stream()
            .filter(r -> r.name().equals("김선생"))
            .findFirst()
            .orElseThrow();
        
        assertThat(kimResult.seventhPeriodSupervisionCount()).isEqualTo(1); // 1일간 7교시 감독
        assertThat(kimResult.totalSupervisionCount()).isEqualTo(4); // 자습2 + 이석1 + 7교시1 = 4일
    }

    private TeacherEntity createAndSaveTeacher(String name, String email) {
        TeacherEntity teacher = TeacherEntity.builder()
                .name(name)
                .mail(email)
                .providerId("provider_" + name)
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        return teacherRepository.save(teacher);
    }

    private void createTestSupervisionSchedules() {
        // 김선생 감독 스케줄
        // 1. 2025-01-01 자습감독 (3개 시간대 - 1일로 카운트되어야 함)
        List<SupervisionScheduleEntity> kim1DaySchedules = List.of(
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 1))
                .period(SchoolPeriod.SEVEN_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 1))
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 1))
                .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build()
        );
        
        // 2. 2025-01-05 자습감독 (3개 시간대)
        List<SupervisionScheduleEntity> kim2DaySchedules = List.of(
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 5))
                .period(SchoolPeriod.SEVEN_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 5))
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 5))
                .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build()
        );
        
        // 3. 2025-01-10 이석감독 (3개 시간대)
        List<SupervisionScheduleEntity> kim3DaySchedules = List.of(
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 10))
                .period(SchoolPeriod.SEVEN_PERIOD).type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 10))
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD).type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 10))
                .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD).type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build()
        );
        
        // 4. 2025-01-15 7교시 감독 (1개 시간대만)
        SupervisionScheduleEntity kim4DaySchedule = SupervisionScheduleEntity.builder()
                .teacher(teacher1).day(LocalDate.of(2025, 1, 15))
                .period(SchoolPeriod.SEVEN_PERIOD).type(SupervisionType.SEVENTH_PERIOD_SUPERVISION)
                .build();

        // 이선생 감독 스케줄
        // 1. 2025-01-02 자습감독 (3개 시간대)
        List<SupervisionScheduleEntity> lee1DaySchedules = List.of(
            SupervisionScheduleEntity.builder()
                .teacher(teacher2).day(LocalDate.of(2025, 1, 2))
                .period(SchoolPeriod.SEVEN_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher2).day(LocalDate.of(2025, 1, 2))
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher2).day(LocalDate.of(2025, 1, 2))
                .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD).type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build()
        );
        
        // 2. 2025-01-08 이석감독 (3개 시간대)
        List<SupervisionScheduleEntity> lee2DaySchedules = List.of(
            SupervisionScheduleEntity.builder()
                .teacher(teacher2).day(LocalDate.of(2025, 1, 8))
                .period(SchoolPeriod.SEVEN_PERIOD).type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher2).day(LocalDate.of(2025, 1, 8))
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD).type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build(),
            SupervisionScheduleEntity.builder()
                .teacher(teacher2).day(LocalDate.of(2025, 1, 8))
                .period(SchoolPeriod.TEN_AND_ELEVEN_PERIOD).type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                .build()
        );

        // 모든 스케줄 저장
        scheduleRepository.saveAll(kim1DaySchedules);
        scheduleRepository.saveAll(kim2DaySchedules);
        scheduleRepository.saveAll(kim3DaySchedules);
        scheduleRepository.save(kim4DaySchedule);
        scheduleRepository.saveAll(lee1DaySchedules);
        scheduleRepository.saveAll(lee2DaySchedules);
        
        // 박선생은 감독 이력 없음 (0일)
    }
}