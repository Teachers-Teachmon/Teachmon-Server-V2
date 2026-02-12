package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.ExitScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ExitRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.ExitScheduleRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("이탈 스케줄 설정 전략 테스트")
class ExitScheduleSettingStrategyTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @Mock
    private ExitRepository exitRepository;

    @Mock
    private ExitScheduleRepository exitScheduleRepository;

    @InjectMocks
    private ExitScheduleSettingStrategy strategy;

    @Test
    @DisplayName("전략의 스케줄 타입은 EXIT여야 한다")
    void shouldReturnExitScheduleType() {
        // When: 스케줄 타입을 가져오면
        ScheduleType scheduleType = strategy.getScheduleType();

        // Then: EXIT여야 한다
        assertThat(scheduleType).isEqualTo(ScheduleType.EXIT);
    }

    @Test
    @DisplayName("baseDate 이후의 이탈 레코드에 대한 스케줄을 설정할 수 있다")
    void shouldSettingExitScheduleForExistingRecords() {
        // Given: baseDate 이후에 이탈 레코드가 있고 학생 스케줄도 존재할 때
        LocalDate baseDate = LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY);

        StudentEntity student = createMockStudent(1L, 1, 1);
        ExitEntity exit = createMockExit(1L, student, baseDate, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule = createMockStudentSchedule(1L, student, baseDate, SchoolPeriod.SEVEN_PERIOD);

        given(exitRepository.findAllFromDate(baseDate)).willReturn(List.of(exit));
        given(studentScheduleRepository.findByStudentAndDayAndPeriod(student, baseDate, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(Optional.of(studentSchedule));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any())).willReturn(0);

        // When: 스케줄을 설정하면
        strategy.settingSchedule(baseDate);

        // Then: 이탈 스케줄이 1개 생성되어야 한다
        verify(exitScheduleRepository, times(1)).save(any(ExitScheduleEntity.class));
    }

    @Test
    @DisplayName("이탈 레코드가 없으면 아무것도 생성하지 않는다")
    void shouldNotCreateSchedulesWhenNoExitRecords() {
        // Given: baseDate 이후에 이탈 레코드가 없을 때
        LocalDate baseDate = LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY);

        given(exitRepository.findAllFromDate(baseDate)).willReturn(List.of());

        // When: 스케줄을 설정하면
        strategy.settingSchedule(baseDate);

        // Then: 아무것도 생성되지 않아야 한다
        verify(exitScheduleRepository, never()).save(any());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("이탈 레코드에 해당하는 학생 스케줄이 없으면 건너뛴다")
    void shouldSkipWhenStudentScheduleNotFound() {
        // Given: 이탈 레코드는 있지만 해당 학생의 학생 스케줄이 없을 때
        LocalDate baseDate = LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY);

        StudentEntity student = createMockStudent(1L, 1, 1);
        ExitEntity exit = createMockExit(1L, student, baseDate, SchoolPeriod.SEVEN_PERIOD);

        given(exitRepository.findAllFromDate(baseDate)).willReturn(List.of(exit));
        given(studentScheduleRepository.findByStudentAndDayAndPeriod(student, baseDate, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(Optional.empty());

        // When: 스케줄을 설정하면
        strategy.settingSchedule(baseDate);

        // Then: 이탈 스케줄이 생성되지 않아야 한다
        verify(exitScheduleRepository, never()).save(any());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    @DisplayName("여러 이탈 레코드가 있을 때 각각 독립적으로 처리된다")
    void shouldHandleMultipleExitRecordsIndependently() {
        // Given: baseDate 이후에 2개의 이탈 레코드가 있을 때
        LocalDate baseDate = LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDate nextTuesday = baseDate.plusDays(1);

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);
        ExitEntity exit1 = createMockExit(1L, student1, baseDate, SchoolPeriod.SEVEN_PERIOD);
        ExitEntity exit2 = createMockExit(2L, student2, nextTuesday, SchoolPeriod.SEVEN_PERIOD);

        StudentScheduleEntity ss1 = createMockStudentSchedule(1L, student1, baseDate, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity ss2 = createMockStudentSchedule(2L, student2, nextTuesday, SchoolPeriod.SEVEN_PERIOD);

        given(exitRepository.findAllFromDate(baseDate)).willReturn(List.of(exit1, exit2));
        given(studentScheduleRepository.findByStudentAndDayAndPeriod(student1, baseDate, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(Optional.of(ss1));
        given(studentScheduleRepository.findByStudentAndDayAndPeriod(student2, nextTuesday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(Optional.of(ss2));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any())).willReturn(0);

        // When: 스케줄을 설정하면
        strategy.settingSchedule(baseDate);

        // Then: 이탈 스케줄이 2개 생성되어야 한다
        verify(exitScheduleRepository, times(2)).save(any(ExitScheduleEntity.class));
    }

    @Test
    @DisplayName("생성된 이탈 스케줄은 기존 이탈 레코드를 참조해야 한다")
    void shouldReferenceExistingExitRecord() {
        // Given: 기존 이탈 레코드가 있을 때
        LocalDate baseDate = LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY);

        StudentEntity student = createMockStudent(1L, 1, 1);
        ExitEntity exit = createMockExit(1L, student, baseDate, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule = createMockStudentSchedule(1L, student, baseDate, SchoolPeriod.SEVEN_PERIOD);

        given(exitRepository.findAllFromDate(baseDate)).willReturn(List.of(exit));
        given(studentScheduleRepository.findByStudentAndDayAndPeriod(student, baseDate, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(Optional.of(studentSchedule));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any())).willReturn(0);

        // When: 스케줄을 설정하면
        strategy.settingSchedule(baseDate);

        // Then: 저장된 이탈 스케줄이 기존 이탈 레코드를 참조해야 한다
        org.mockito.ArgumentCaptor<ExitScheduleEntity> captor =
                org.mockito.ArgumentCaptor.forClass(ExitScheduleEntity.class);
        verify(exitScheduleRepository).save(captor.capture());

        assertThat(captor.getValue().getExit()).isEqualTo(exit);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private StudentEntity createMockStudent(Long id, Integer grade, Integer classNumber) {
        StudentEntity student = mock(StudentEntity.class);
        given(student.getId()).willReturn(id);
        given(student.getGrade()).willReturn(grade);
        given(student.getClassNumber()).willReturn(classNumber);
        return student;
    }

    private ExitEntity createMockExit(Long id, StudentEntity student, LocalDate day, SchoolPeriod period) {
        ExitEntity exit = mock(ExitEntity.class);
        given(exit.getId()).willReturn(id);
        given(exit.getStudent()).willReturn(student);
        given(exit.getDay()).willReturn(day);
        given(exit.getPeriod()).willReturn(period);
        return exit;
    }

    private StudentScheduleEntity createMockStudentSchedule(Long id, StudentEntity student,
                                                              LocalDate day, SchoolPeriod period) {
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        given(studentSchedule.getId()).willReturn(id);
        given(studentSchedule.getStudent()).willReturn(student);
        given(studentSchedule.getDay()).willReturn(day);
        given(studentSchedule.getPeriod()).willReturn(period);
        return studentSchedule;
    }
}
