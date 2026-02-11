package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AfterSchoolScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AfterSchoolScheduleRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("방과후 보강 스케줄 설정 전략 테스트")
class AfterSchoolReinforcementScheduleSettingStrategyTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;

    @Mock
    private AfterSchoolScheduleRepository afterSchoolScheduleRepository;

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @InjectMocks
    private AfterSchoolReinforcementScheduleSettingStrategy strategy;

    @Test
    @DisplayName("전략의 스케줄 타입은 AFTER_SCHOOL_REINFORCEMENT여야 한다")
    void shouldReturnAfterSchoolReinforcementScheduleType() {
        // When: 스케줄 타입을 가져오면
        ScheduleType scheduleType = strategy.getScheduleType();

        // Then: AFTER_SCHOOL_REINFORCEMENT여야 한다
        assertThat(scheduleType).isEqualTo(ScheduleType.AFTER_SCHOOL_REINFORCEMENT);
    }

    @Test
    @DisplayName("다음 주의 방과후 보강을 일반 방과후 스케줄로 설정할 수 있다")
    void shouldSettingAfterSchoolReinforcementAsRegularAfterSchoolSchedule() {
        // Given: 다음 주에 방과후 보강이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        AfterSchoolEntity afterSchool = createMockAfterSchool(1L, "수학 방과후");
        PlaceEntity place = createMockPlace(1L, "수학실");
        AfterSchoolReinforcementEntity reinforcement = createMockAfterSchoolReinforcement(
                1L, afterSchool, place, nextMonday, SchoolPeriod.SEVEN_PERIOD
        );

        StudentEntity student = createMockStudent(1L, 1, 1);
        StudentScheduleEntity studentSchedule = createMockStudentSchedule(1L, student, nextMonday, SchoolPeriod.SEVEN_PERIOD);

        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(nextMonday, nextSunday))
                .willReturn(List.of(reinforcement));
        given(studentScheduleRepository.findAllByAfterSchoolAndDayAndPeriod(afterSchool, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(1L))
                .willReturn(0);

        // When: 스케줄을 설정하면 (nextMonday를 baseDate로 사용하여 isBefore 체크 통과)
        strategy.settingSchedule(nextMonday);

        // Then: 일반 방과후 스케줄이 생성되어야 한다 (보강용 별도 엔티티가 아님)
        verify(afterSchoolScheduleRepository, times(1)).save(any(AfterSchoolScheduleEntity.class));
    }

    @Test
    @DisplayName("여러 학생에 대한 방과후 보강 스케줄을 설정할 수 있다")
    void shouldSettingAfterSchoolReinforcementScheduleForMultipleStudents() {
        // Given: 다음 주에 방과후 보강이 있고, 여러 학생이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        AfterSchoolEntity afterSchool = createMockAfterSchool(1L, "수학 방과후");
        PlaceEntity place = createMockPlace(1L, "수학실");
        AfterSchoolReinforcementEntity reinforcement = createMockAfterSchoolReinforcement(
                1L, afterSchool, place, nextMonday, SchoolPeriod.SEVEN_PERIOD
        );

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);
        StudentEntity student3 = createMockStudent(3L, 1, 3);
        StudentScheduleEntity studentSchedule1 = createMockStudentSchedule(1L, student1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule2 = createMockStudentSchedule(2L, student2, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule3 = createMockStudentSchedule(3L, student3, nextMonday, SchoolPeriod.SEVEN_PERIOD);

        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(nextMonday, nextSunday))
                .willReturn(List.of(reinforcement));
        given(studentScheduleRepository.findAllByAfterSchoolAndDayAndPeriod(afterSchool, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule1, studentSchedule2, studentSchedule3));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any()))
                .willReturn(0);

        // When: 스케줄을 설정하면 (nextMonday를 baseDate로 사용하여 isBefore 체크 통과)
        strategy.settingSchedule(nextMonday);

        // Then: 3개의 방과후 스케줄이 생성되어야 한다
        verify(afterSchoolScheduleRepository, times(3)).save(any(AfterSchoolScheduleEntity.class));
    }

    @Test
    @DisplayName("여러 방과후 보강이 있을 때 각각 독립적으로 처리되어야 한다")
    void shouldHandleMultipleReinforcementsIndependently() {
        // Given: 2개의 방과후 보강이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextTuesday = nextWeek.with(DayOfWeek.TUESDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        AfterSchoolEntity afterSchool1 = createMockAfterSchool(1L, "수학 방과후");
        AfterSchoolEntity afterSchool2 = createMockAfterSchool(2L, "과학 방과후");
        PlaceEntity place1 = createMockPlace(1L, "수학실");
        PlaceEntity place2 = createMockPlace(2L, "과학실");

        AfterSchoolReinforcementEntity reinforcement1 = createMockAfterSchoolReinforcement(
                1L, afterSchool1, place1, nextMonday, SchoolPeriod.SEVEN_PERIOD
        );
        AfterSchoolReinforcementEntity reinforcement2 = createMockAfterSchoolReinforcement(
                2L, afterSchool2, place2, nextTuesday, SchoolPeriod.EIGHT_AND_NINE_PERIOD
        );

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);

        StudentScheduleEntity studentSchedule1 = createMockStudentSchedule(1L, student1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule2 = createMockStudentSchedule(2L, student2, nextTuesday, SchoolPeriod.EIGHT_AND_NINE_PERIOD);

        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(nextMonday, nextSunday))
                .willReturn(List.of(reinforcement1, reinforcement2));
        given(studentScheduleRepository.findAllByAfterSchoolAndDayAndPeriod(afterSchool1, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule1));
        given(studentScheduleRepository.findAllByAfterSchoolAndDayAndPeriod(afterSchool2, nextTuesday, SchoolPeriod.EIGHT_AND_NINE_PERIOD))
                .willReturn(List.of(studentSchedule2));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any()))
                .willReturn(0);

        // When: 스케줄을 설정하면 (nextMonday를 baseDate로 사용하여 isBefore 체크 통과)
        strategy.settingSchedule(nextMonday);

        // Then: 2개의 방과후 스케줄이 생성되어야 한다
        verify(afterSchoolScheduleRepository, times(2)).save(any(AfterSchoolScheduleEntity.class));
    }

    @Test
    @DisplayName("방과후 보강이 없으면 아무것도 생성하지 않는다")
    void shouldNotCreateSchedulesWhenNoReinforcements() {
        // Given: 다음 주에 방과후 보강이 없을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(nextMonday, nextSunday))
                .willReturn(List.of());

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 아무것도 생성되지 않아야 한다
        verify(afterSchoolScheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("보강 스케줄은 changeDay와 changePeriod 기준으로 학생 스케줄을 찾는다")
    void shouldFindStudentSchedulesBasedOnChangeDayAndPeriod() {
        // Given: 보강이 원래 화요일 7교시 방과후를 수요일 8-9교시로 변경했을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(DayOfWeek.MONDAY);
        LocalDate nextWednesday = nextWeek.with(DayOfWeek.WEDNESDAY);
        LocalDate nextSunday = nextWeek.with(DayOfWeek.SUNDAY);

        AfterSchoolEntity afterSchool = createMockAfterSchool(1L, "영어 방과후");
        PlaceEntity place = createMockPlace(1L, "영어실");

        // 원래는 화요일 7교시였지만, 보강은 수요일 8-9교시로 변경
        AfterSchoolReinforcementEntity reinforcement = createMockAfterSchoolReinforcement(
                1L, afterSchool, place, nextWednesday, SchoolPeriod.EIGHT_AND_NINE_PERIOD
        );

        StudentEntity student = createMockStudent(1L, 1, 1);
        StudentScheduleEntity studentSchedule = createMockStudentSchedule(
                1L, student, nextWednesday, SchoolPeriod.EIGHT_AND_NINE_PERIOD
        );

        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(nextMonday, nextSunday))
                .willReturn(List.of(reinforcement));
        given(studentScheduleRepository.findAllByAfterSchoolAndDayAndPeriod(
                afterSchool, nextWednesday, SchoolPeriod.EIGHT_AND_NINE_PERIOD))
                .willReturn(List.of(studentSchedule));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(1L))
                .willReturn(0);

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 변경된 날짜/교시로 학생 스케줄을 조회해야 한다
        verify(studentScheduleRepository).findAllByAfterSchoolAndDayAndPeriod(
                afterSchool, nextWednesday, SchoolPeriod.EIGHT_AND_NINE_PERIOD
        );
        verify(afterSchoolScheduleRepository, times(1)).save(any(AfterSchoolScheduleEntity.class));
    }

    private AfterSchoolEntity createMockAfterSchool(Long id, String name) {
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);
        given(afterSchool.getId()).willReturn(id);
        given(afterSchool.getName()).willReturn(name);
        return afterSchool;
    }

    private PlaceEntity createMockPlace(Long id, String name) {
        PlaceEntity place = mock(PlaceEntity.class);
        given(place.getId()).willReturn(id);
        given(place.getName()).willReturn(name);
        return place;
    }

    private AfterSchoolReinforcementEntity createMockAfterSchoolReinforcement(
            Long id,
            AfterSchoolEntity afterSchool,
            PlaceEntity place,
            LocalDate changeDay,
            SchoolPeriod changePeriod
    ) {
        AfterSchoolReinforcementEntity reinforcement = mock(AfterSchoolReinforcementEntity.class);
        given(reinforcement.getId()).willReturn(id);
        given(reinforcement.getAfterSchool()).willReturn(afterSchool);
        given(reinforcement.getPlace()).willReturn(place);
        given(reinforcement.getChangeDay()).willReturn(changeDay);
        given(reinforcement.getChangePeriod()).willReturn(changePeriod);
        return reinforcement;
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
}
