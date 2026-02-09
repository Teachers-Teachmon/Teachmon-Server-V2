package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AdditionalSelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AdditionalSelfStudyScheduleRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("추가 자습 스케줄 설정 전략 테스트")
class AdditionalSelfStudyScheduleSettingStrategyTest {

    @Mock
    private AdditionalSelfStudyRepository additionalSelfStudyRepository;

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private AdditionalSelfStudyScheduleRepository additionalSelfStudyScheduleRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private AdditionalSelfStudyScheduleSettingStrategy strategy;

    @Test
    @DisplayName("전략의 스케줄 타입은 ADDITIONAL_SELF_STUDY여야 한다")
    void shouldReturnAdditionalSelfStudyScheduleType() {
        // When: 스케줄 타입을 가져오면
        ScheduleType scheduleType = strategy.getScheduleType();

        // Then: ADDITIONAL_SELF_STUDY여야 한다
        assertThat(scheduleType).isEqualTo(ScheduleType.ADDITIONAL_SELF_STUDY);
    }

    @Test
    @DisplayName("다음 주의 추가 자습 스케줄을 설정할 수 있다")
    void shouldSettingAdditionalSelfStudySchedule() {
        // Given: 다음 주에 추가 자습이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        AdditionalSelfStudyEntity additionalSelfStudy = createMockAdditionalSelfStudy(1L, 1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentEntity student = createMockStudent(1L, 1, 1);
        StudentScheduleEntity studentSchedule = createMockStudentSchedule(1L, student, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        PlaceEntity place = createMockPlace(1L, "1-1");

        given(additionalSelfStudyRepository.findAllByDayBetween(nextMonday, nextSunday))
                .willReturn(List.of(additionalSelfStudy));
        given(studentScheduleRepository.findAllByGradeAndDayAndPeriod(1, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(1L))
                .willReturn(0);
        given(placeRepository.findAllByGradePrefix(1))
                .willReturn(Map.of(1, place));
        given(placeRepository.checkPlaceAvailability(nextMonday, SchoolPeriod.SEVEN_PERIOD, place))
                .willReturn(false);

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 추가 자습 스케줄이 생성되어야 한다
        verify(additionalSelfStudyScheduleRepository, times(1)).save(any(AdditionalSelfStudyScheduleEntity.class));
    }

    @Test
    @DisplayName("BUG: 생성된 Schedule의 타입은 ADDITIONAL_SELF_STUDY여야 한다 (현재는 SELF_STUDY)")
    void shouldCreateScheduleWithCorrectType() {
        // Given: 다음 주에 추가 자습이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        AdditionalSelfStudyEntity additionalSelfStudy = createMockAdditionalSelfStudy(1L, 1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentEntity student = createMockStudent(1L, 1, 1);
        StudentScheduleEntity studentSchedule = createMockStudentSchedule(1L, student, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        PlaceEntity place = createMockPlace(1L, "1-1");

        given(additionalSelfStudyRepository.findAllByDayBetween(nextMonday, nextSunday))
                .willReturn(List.of(additionalSelfStudy));
        given(studentScheduleRepository.findAllByGradeAndDayAndPeriod(1, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(1L))
                .willReturn(0);
        given(placeRepository.findAllByGradePrefix(1))
                .willReturn(Map.of(1, place));
        given(placeRepository.checkPlaceAvailability(nextMonday, SchoolPeriod.SEVEN_PERIOD, place))
                .willReturn(false);

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 저장된 Schedule의 타입을 검증한다
        ArgumentCaptor<ScheduleEntity> scheduleCaptor = ArgumentCaptor.forClass(ScheduleEntity.class);
        verify(scheduleRepository).save(scheduleCaptor.capture());

        ScheduleEntity savedSchedule = scheduleCaptor.getValue();

        // 🐛 BUG: 현재 코드는 ScheduleType.SELF_STUDY를 사용하지만,
        // ADDITIONAL_SELF_STUDY를 사용해야 합니다!
        // 이 테스트는 버그가 수정되기 전까지 실패할 것입니다.
        assertThat(savedSchedule.getType())
                .as("생성된 Schedule의 타입은 ADDITIONAL_SELF_STUDY여야 합니다")
                .isEqualTo(ScheduleType.ADDITIONAL_SELF_STUDY);  // ❌ 현재는 SELF_STUDY
    }

    @Test
    @DisplayName("여러 학생에 대한 추가 자습 스케줄을 설정할 수 있다")
    void shouldSettingAdditionalSelfStudyScheduleForMultipleStudents() {
        // Given: 다음 주에 추가 자습이 있고, 여러 학생이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        AdditionalSelfStudyEntity additionalSelfStudy = createMockAdditionalSelfStudy(1L, 1, nextMonday, SchoolPeriod.SEVEN_PERIOD);

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);
        StudentScheduleEntity studentSchedule1 = createMockStudentSchedule(1L, student1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule2 = createMockStudentSchedule(2L, student2, nextMonday, SchoolPeriod.SEVEN_PERIOD);

        PlaceEntity place1 = createMockPlace(1L, "1-1");
        PlaceEntity place2 = createMockPlace(2L, "1-2");

        given(additionalSelfStudyRepository.findAllByDayBetween(nextMonday, nextSunday))
                .willReturn(List.of(additionalSelfStudy));
        given(studentScheduleRepository.findAllByGradeAndDayAndPeriod(1, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule1, studentSchedule2));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any()))
                .willReturn(0);
        given(placeRepository.findAllByGradePrefix(1))
                .willReturn(Map.of(1, place1, 2, place2));
        given(placeRepository.checkPlaceAvailability(any(), any(), any()))
                .willReturn(false);

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 2개의 추가 자습 스케줄이 생성되어야 한다
        verify(additionalSelfStudyScheduleRepository, times(2)).save(any(AdditionalSelfStudyScheduleEntity.class));
    }

    private AdditionalSelfStudyEntity createMockAdditionalSelfStudy(Long id, Integer grade, LocalDate day, SchoolPeriod period) {
        AdditionalSelfStudyEntity additionalSelfStudy = mock(AdditionalSelfStudyEntity.class);
        given(additionalSelfStudy.getId()).willReturn(id);
        given(additionalSelfStudy.getGrade()).willReturn(grade);
        given(additionalSelfStudy.getDay()).willReturn(day);
        given(additionalSelfStudy.getPeriod()).willReturn(period);
        return additionalSelfStudy;
    }

    private StudentEntity createMockStudent(Long id, Integer grade, Integer classNumber) {
        StudentEntity student = mock(StudentEntity.class);
        given(student.getId()).willReturn(id);
        given(student.getGrade()).willReturn(grade);
        given(student.getClassNumber()).willReturn(classNumber);
        return student;
    }

    private StudentScheduleEntity createMockStudentSchedule(Long id, StudentEntity student, LocalDate day, SchoolPeriod period) {
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        given(studentSchedule.getId()).willReturn(id);
        given(studentSchedule.getStudent()).willReturn(student);
        given(studentSchedule.getDay()).willReturn(day);
        given(studentSchedule.getPeriod()).willReturn(period);
        return studentSchedule;
    }

    private PlaceEntity createMockPlace(Long id, String name) {
        PlaceEntity place = mock(PlaceEntity.class);
        given(place.getId()).willReturn(id);
        given(place.getName()).willReturn(name);
        return place;
    }
}
