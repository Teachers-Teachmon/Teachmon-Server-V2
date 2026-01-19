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
@DisplayName("감독 일정 저장소 - 선생님별 감독 일정 카운트 테스트")
class SupervisionScheduleRepositoryGetTest {

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
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision(null);

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
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision(null);

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
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision(null);

        // Then: 해당 선생님의 감독 일정 수가 정확하게 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("김선생");
        assertThat(result.getFirst().supervisionCount()).isEqualTo(2);
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
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision(null);

        // Then: 모든 감독 일정이 카운트된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().supervisionCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("이름으로 선생님을 검색할 수 있다")
    void shouldSearchTeachersByName() {
        // Given: 여러 선생님이 감독 일정을 가지고 있을 때
        TeacherEntity teacher1 = createAndSaveTeacher("김선생", "kim@teacher.com");
        TeacherEntity teacher2 = createAndSaveTeacher("이선생", "lee@teacher.com");
        TeacherEntity teacher3 = createAndSaveTeacher("김영희", "kim.young@teacher.com");

        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 11), SchoolPeriod.EIGHT_AND_NINE_PERIOD);
        createAndSaveSupervision(teacher2, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 10), SchoolPeriod.TEN_AND_ELEVEN_PERIOD);

        // When: "김"으로 검색하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision("김");

        // Then: 이름에 "김"이 포함된 선생님만 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TeacherListResponse::name)
                .containsExactlyInAnyOrder("김선생", "김영희");
    }

    @Test
    @DisplayName("빈 문자열로 검색하면 모든 선생님이 조회된다")
    void shouldGetAllTeachersWhenQueryIsEmpty() {
        // Given: 여러 선생님이 감독 일정을 가지고 있을 때
        TeacherEntity teacher1 = createAndSaveTeacher("김선생", "kim@teacher.com");
        TeacherEntity teacher2 = createAndSaveTeacher("이선생", "lee@teacher.com");
        TeacherEntity teacher3 = createAndSaveTeacher("박선생", "park@teacher.com");

        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher2, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher3, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);

        // When: 빈 문자열로 검색하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision("");

        // Then: 모든 선생님이 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(TeacherListResponse::name)
                .containsExactlyInAnyOrder("김선생", "이선생", "박선생");
    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
    void shouldReturnEmptyListWhenNoMatchingTeachers() {
        // Given: 여러 선생님이 감독 일정을 가지고 있을 때
        TeacherEntity teacher1 = createAndSaveTeacher("김선생", "kim@teacher.com");
        TeacherEntity teacher2 = createAndSaveTeacher("이선생", "lee@teacher.com");

        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher2, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);

        // When: 존재하지 않는 이름으로 검색하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision("최");

        // Then: 빈 목록이 반환된다
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("대소문자를 구분하지 않고 검색할 수 있다")
    void shouldSearchCaseInsensitive() {
        // Given: 영문 이름을 가진 선생님이 감독 일정을 가지고 있을 때
        TeacherEntity teacher1 = createAndSaveTeacher("John Smith", "john@teacher.com");
        TeacherEntity teacher2 = createAndSaveTeacher("Jane Doe", "jane@teacher.com");

        createAndSaveSupervision(teacher1, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);
        createAndSaveSupervision(teacher2, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);

        // When: 소문자로 검색하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision("john");

        // Then: 대소문자 구분 없이 검색된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("John Smith");
    }

    @Test
    @DisplayName("감독 일정이 없는 선생님도 조회된다")
    void shouldIncludeTeachersWithoutSupervision() {
        // Given: 감독 일정이 있는 선생님과 없는 선생님이 있을 때
        TeacherEntity teacherWithSchedule = createAndSaveTeacher("김선생", "kim@teacher.com");
        TeacherEntity teacherWithoutSchedule = createAndSaveTeacher("이선생", "lee@teacher.com");

        createAndSaveSupervision(teacherWithSchedule, LocalDate.of(2024, 1, 10), SchoolPeriod.SEVEN_PERIOD);

        // When: 모든 선생님을 조회하면
        List<TeacherListResponse> result = supervisionScheduleRepository.countTeacherSupervision(null);

        // Then: 감독 일정이 없는 선생님도 포함된다 (supervisionCount = 0)
        assertThat(result).hasSize(2);

        TeacherListResponse withSchedule = result.stream()
                .filter(r -> r.name().equals("김선생"))
                .findFirst()
                .orElseThrow();
        assertThat(withSchedule.supervisionCount()).isEqualTo(1);

        TeacherListResponse withoutSchedule = result.stream()
                .filter(r -> r.name().equals("이선생"))
                .findFirst()
                .orElseThrow();
        assertThat(withoutSchedule.supervisionCount()).isEqualTo(0);
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
