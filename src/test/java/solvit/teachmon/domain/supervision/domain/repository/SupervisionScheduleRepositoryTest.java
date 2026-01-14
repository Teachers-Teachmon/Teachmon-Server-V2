package solvit.teachmon.domain.supervision.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("감독 일정 저장소 테스트")
class SupervisionScheduleRepositoryTest {

    @Autowired
    private SupervisionScheduleRepository supervisionScheduleRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    @DisplayName("선생님별 감독 일정 수를 카운트할 수 있다")
    void shouldCountTeacherSupervision() {
        // Given: 선생님 3명이 각각 다른 수의 감독 일정을 가지고 있을 때
        TeacherEntity teacher1 = createAndSaveTeacher("김선생", "kim@teacher.com");
        TeacherEntity teacher2 = createAndSaveTeacher("이선생", "lee@teacher.com");
        TeacherEntity teacher3 = createAndSaveTeacher("박선생", "park@teacher.com");

        // 김선생: 5개의 감독 일정
        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 11), SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 12), SchoolPeriod.TEN_AND_ELEVEN_PERIOD);
        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 13), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 14), SchoolPeriod.EIGHT_AND_NINE_PERIOD);

        // 이선생: 3개의 감독 일정
        createAndSaveSupervision(teacher2, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher2, LocalDate.of(2024, 1, 11), SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        createAndSaveSupervision(teacher2, LocalDate.of(2024, 1, 12), SchoolPeriod.TEN_AND_ELEVEN_PERIOD);

        // 박선생: 7개의 감독 일정
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 11), SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 12), SchoolPeriod.TEN_AND_ELEVEN_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 13), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 14), SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 15), SchoolPeriod.TEN_AND_ELEVEN_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 16), SchoolPeriod.SEVEN_PERIOD);

        // When: 선생님별 감독 일정 수를 카운트하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision();

        // Then: 각 선생님의 감독 일정 수가 정확하게 카운트된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        TeacherListResponse kim = result.stream()
                .filter(r -> r.name().equals("김선생"))
                .findFirst()
                .orElseThrow();
        assertThat(kim.supervisionCount()).isEqualTo(5);
        assertThat(kim.email()).isEqualTo("kim@teacher.com");

        TeacherListResponse lee = result.stream()
                .filter(r -> r.name().equals("이선생"))
                .findFirst()
                .orElseThrow();
        assertThat(lee.supervisionCount()).isEqualTo(3);
        assertThat(lee.email()).isEqualTo("lee@teacher.com");

        TeacherListResponse park = result.stream()
                .filter(r -> r.name().equals("박선생"))
                .findFirst()
                .orElseThrow();
        assertThat(park.supervisionCount()).isEqualTo(7);
        assertThat(park.email()).isEqualTo("park@teacher.com");
    }

    @Test
    @DisplayName("감독 일정이 없을 때 빈 목록을 반환한다")
    void shouldReturnEmptyListWhenNoSupervisionExists() {
        // Given: 감독 일정이 없을 때

        // When: 선생님별 감독 일정 수를 카운트하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision();

        // Then: 빈 목록이 반환된다
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("한 선생님만 감독 일정이 있을 때도 정상적으로 조회된다")
    void shouldCountWhenOnlyOneTeacherHasSupervision() {
        // Given: 한 선생님만 감독 일정이 있을 때
        TeacherEntity teacher = createAndSaveTeacher("김선생", "kim@teacher.com");
        createAndSaveSupervision(teacher, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher, LocalDate.of(2024, 1, 11), SchoolPeriod.EIGHT_AND_NINE_PERIOD);

        // When: 선생님별 감독 일정 수를 카운트하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision();

        // Then: 해당 선생님의 감독 일정 수가 정확하게 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("김선생");
        assertThat(result.get(0).supervisionCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("같은 날 다른 시간대에 여러 감독 일정이 있을 때 모두 카운트된다")
    void shouldCountAllSupervisionsInSameDay() {
        // Given: 한 선생님이 같은 날 여러 시간대에 감독 일정이 있을 때
        TeacherEntity teacher = createAndSaveTeacher("김선생", "kim@teacher.com");
        LocalDate today = LocalDate.of(2024, 1, 10);
        createAndSaveSupervision(teacher, today, SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher, today, SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        createAndSaveSupervision(teacher, today, SchoolPeriod.TEN_AND_ELEVEN_PERIOD);

        // When: 선생님별 감독 일정 수를 카운트하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision();

        // Then: 모든 감독 일정이 카운트된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).supervisionCount()).isEqualTo(3);
    }

    private TeacherEntity createAndSaveTeacher(String name, String email) {
        TeacherEntity teacher = TeacherEntity.builder()
                .name(name)
                .mail(email)
                .profile(name + " 프로필")
                .build();
        return teacherRepository.save(teacher);
    }

    private void createAndSaveSupervision(TeacherEntity teacher, LocalDate day, SchoolPeriod period) {
        SupervisionScheduleEntity supervision = SupervisionScheduleEntity.builder()
                .teacher(teacher)
                .day(day)
                .period(period)
                .type(SupervisionType.SELF_STUDY_SUPERVISION)
                .build();
        supervisionScheduleRepository.save(supervision);
    }
}