package solvit.teachmon.domain.student_schedule.application.service;

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
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategyComposite;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("학생 스케줄 설정 서비스 테스트")
class StudentScheduleSettingServiceTest {

    @Mock
    private StudentScheduleSettingStrategyComposite studentScheduleSettingStrategyComposite;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @InjectMocks
    private StudentScheduleSettingService studentScheduleSettingService;

    @Mock
    private StudentScheduleSettingStrategy mockStrategy1;

    @Mock
    private StudentScheduleSettingStrategy mockStrategy2;

    @Test
    @DisplayName("새로운 학생 스케줄을 생성할 수 있다")
    void shouldCreateNewStudentSchedule() {
        // Given: 현재 연도의 학생들이 있을 때
        LocalDate today = LocalDate.now();
        Integer currentYear = today.getYear();

        StudentEntity student1 = createMockStudent(1L, currentYear, 1, 1);
        StudentEntity student2 = createMockStudent(2L, currentYear, 1, 2);

        given(studentRepository.findByYear(currentYear))
                .willReturn(List.of(student1, student2));

        // When: 새로운 학생 스케줄을 생성하면
        studentScheduleSettingService.createNewStudentSchedule();

        // Then: 과거 스케줄을 삭제하고, 새로운 스케줄을 저장해야 한다
        LocalDate nextMonday = today.with(DayOfWeek.MONDAY).plusWeeks(1);
        LocalDate nextSunday = today.with(DayOfWeek.SUNDAY).plusWeeks(1);

        verify(studentScheduleRepository).deleteAllByDayBetween(nextMonday, nextSunday);

        ArgumentCaptor<List<StudentScheduleEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(studentScheduleRepository).saveAll(captor.capture());

        List<StudentScheduleEntity> savedSchedules = captor.getValue();

        // 2명의 학생 * 4일 (월~목) * 3개 교시 (7교시, 8~9교시, 10~11교시) = 24개
        assertThat(savedSchedules).hasSize(24);
    }

    @Test
    @DisplayName("생성된 스케줄은 다음 주 월요일부터 목요일까지여야 한다")
    void shouldCreateSchedulesForNextWeekMondayToThursday() {
        // Given: 한 명의 학생이 있을 때
        LocalDate today = LocalDate.now();
        Integer currentYear = today.getYear();

        StudentEntity student = createMockStudent(1L, currentYear, 1, 1);
        given(studentRepository.findByYear(currentYear))
                .willReturn(List.of(student));

        // When: 새로운 학생 스케줄을 생성하면
        studentScheduleSettingService.createNewStudentSchedule();

        // Then: 저장된 스케줄의 날짜를 검증한다
        ArgumentCaptor<List<StudentScheduleEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(studentScheduleRepository).saveAll(captor.capture());

        List<StudentScheduleEntity> savedSchedules = captor.getValue();

        LocalDate nextMonday = today.with(DayOfWeek.MONDAY).plusWeeks(1);
        LocalDate nextTuesday = today.with(DayOfWeek.TUESDAY).plusWeeks(1);
        LocalDate nextWednesday = today.with(DayOfWeek.WEDNESDAY).plusWeeks(1);
        LocalDate nextThursday = today.with(DayOfWeek.THURSDAY).plusWeeks(1);

        assertThat(savedSchedules)
                .extracting(StudentScheduleEntity::getDay)
                .containsOnly(nextMonday, nextTuesday, nextWednesday, nextThursday);
    }

    @Test
    @DisplayName("생성된 스케줄은 방과후 활동 시간대(7교시, 8-9교시, 10-11교시)만 포함해야 한다")
    void shouldCreateSchedulesOnlyForAfterSchoolPeriods() {
        // Given: 한 명의 학생이 있을 때
        LocalDate today = LocalDate.now();
        Integer currentYear = today.getYear();

        StudentEntity student = createMockStudent(1L, currentYear, 1, 1);
        given(studentRepository.findByYear(currentYear))
                .willReturn(List.of(student));

        // When: 새로운 학생 스케줄을 생성하면
        studentScheduleSettingService.createNewStudentSchedule();

        // Then: 저장된 스케줄의 교시를 검증한다
        ArgumentCaptor<List<StudentScheduleEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(studentScheduleRepository).saveAll(captor.capture());

        List<StudentScheduleEntity> savedSchedules = captor.getValue();

        assertThat(savedSchedules)
                .extracting(StudentScheduleEntity::getPeriod)
                .containsOnly(
                        SchoolPeriod.SEVEN_PERIOD,
                        SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                        SchoolPeriod.TEN_AND_ELEVEN_PERIOD
                );
    }

    @Test
    @DisplayName("현재 연도가 아닌 학생들은 스케줄이 생성되지 않아야 한다")
    void shouldNotCreateSchedulesForStudentsNotInCurrentYear() {
        // Given: 현재 연도의 학생이 없을 때
        LocalDate today = LocalDate.now();
        Integer currentYear = today.getYear();

        given(studentRepository.findByYear(currentYear))
                .willReturn(List.of());

        // When: 새로운 학생 스케줄을 생성하면
        studentScheduleSettingService.createNewStudentSchedule();

        // Then: 빈 리스트가 저장되어야 한다
        ArgumentCaptor<List<StudentScheduleEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(studentScheduleRepository).saveAll(captor.capture());

        List<StudentScheduleEntity> savedSchedules = captor.getValue();
        assertThat(savedSchedules).isEmpty();
    }

    @Test
    @DisplayName("모든 타입의 스케줄을 설정할 수 있다")
    void shouldSettingAllTypeSchedule() {
        // Given: 여러 개의 설정 전략이 있을 때
        given(studentScheduleSettingStrategyComposite.getAllStrategies())
                .willReturn(List.of(mockStrategy1, mockStrategy2));

        // When: 모든 타입의 스케줄을 설정하면
        studentScheduleSettingService.settingAllTypeSchedule();

        // Then: 모든 전략의 settingSchedule이 호출되어야 한다
        verify(mockStrategy1, times(1)).settingSchedule();
        verify(mockStrategy2, times(1)).settingSchedule();
    }

    @Test
    @DisplayName("전략이 없으면 아무것도 설정하지 않는다")
    void shouldDoNothingWhenNoStrategies() {
        // Given: 설정 전략이 없을 때
        given(studentScheduleSettingStrategyComposite.getAllStrategies())
                .willReturn(List.of());

        // When: 모든 타입의 스케줄을 설정하면
        studentScheduleSettingService.settingAllTypeSchedule();

        // Then: 아무런 전략도 호출되지 않아야 한다
        verifyNoInteractions(mockStrategy1, mockStrategy2);
    }

    private StudentEntity createMockStudent(Long id, Integer year, Integer grade, Integer classNumber) {
        StudentEntity student = mock(StudentEntity.class);
        given(student.getId()).willReturn(id);
        given(student.getYear()).willReturn(year);
        given(student.getGrade()).willReturn(grade);
        given(student.getClassNumber()).willReturn(classNumber);
        return student;
    }
}
