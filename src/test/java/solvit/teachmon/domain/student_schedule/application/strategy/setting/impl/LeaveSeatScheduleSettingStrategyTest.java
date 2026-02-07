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
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.repository.FixedLeaveSeatRepository;
import solvit.teachmon.domain.leave_seat.domain.repository.FixedLeaveSeatStudentRepository;
import solvit.teachmon.domain.leave_seat.domain.repository.LeaveSeatRepository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.LeaveSeatScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.LeaveSeatScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("이석 스케줄 설정 전략 테스트")
class LeaveSeatScheduleSettingStrategyTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @Mock
    private LeaveSeatRepository leaveSeatRepository;

    @Mock
    private LeaveSeatScheduleRepository leaveSeatScheduleRepository;

    @Mock
    private FixedLeaveSeatRepository fixedLeaveSeatRepository;

    @Mock
    private FixedLeaveSeatStudentRepository fixedLeaveSeatStudentRepository;

    @InjectMocks
    private LeaveSeatScheduleSettingStrategy strategy;

    @Test
    @DisplayName("전략의 스케줄 타입은 LEAVE_SEAT여야 한다")
    void shouldReturnLeaveSeatScheduleType() {
        // When: 스케줄 타입을 가져오면
        ScheduleType scheduleType = strategy.getScheduleType();

        // Then: LEAVE_SEAT여야 한다
        assertThat(scheduleType).isEqualTo(ScheduleType.LEAVE_SEAT);
    }

    @Test
    @DisplayName("고정 이석에 대한 스케줄을 설정할 수 있다")
    void shouldSettingLeaveSeatSchedule() {
        // Given: 고정 이석이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(WeekDay.MON.toDayOfWeek());

        TeacherEntity teacher = createMockTeacher(1L);
        PlaceEntity place = createMockPlace(1L, "도서관");
        FixedLeaveSeatEntity fixedLeaveSeat = createMockFixedLeaveSeat(1L, teacher, place, WeekDay.MON, SchoolPeriod.SEVEN_PERIOD, "특별활동");

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);

        StudentScheduleEntity studentSchedule1 = createMockStudentSchedule(1L, student1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule2 = createMockStudentSchedule(2L, student2, nextMonday, SchoolPeriod.SEVEN_PERIOD);

        given(fixedLeaveSeatRepository.findAll())
                .willReturn(List.of(fixedLeaveSeat));
        given(studentScheduleRepository.findAllByFixedLeaveSeatAndDay(fixedLeaveSeat, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule1, studentSchedule2));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any()))
                .willReturn(0);
        given(fixedLeaveSeatStudentRepository.findAllByFixedLeaveSeat(fixedLeaveSeat))
                .willReturn(List.of(student1, student2));

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 이석 스케줄이 생성되어야 한다
        verify(leaveSeatScheduleRepository, times(2)).save(any(LeaveSeatScheduleEntity.class));
    }

    @Test
    @DisplayName("BUG: LeaveSeat은 학생마다 생성되는 것이 아니라, FixedLeaveSeat마다 1개만 생성되어야 한다")
    void shouldCreateOnlyOneLeaveSeatPerFixedLeaveSeat() {
        // Given: 고정 이석에 2명의 학생이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(WeekDay.MON.toDayOfWeek());

        TeacherEntity teacher = createMockTeacher(1L);
        PlaceEntity place = createMockPlace(1L, "도서관");
        FixedLeaveSeatEntity fixedLeaveSeat = createMockFixedLeaveSeat(1L, teacher, place, WeekDay.MON, SchoolPeriod.SEVEN_PERIOD, "특별활동");

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);

        StudentScheduleEntity studentSchedule1 = createMockStudentSchedule(1L, student1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule2 = createMockStudentSchedule(2L, student2, nextMonday, SchoolPeriod.SEVEN_PERIOD);

        given(fixedLeaveSeatRepository.findAll())
                .willReturn(List.of(fixedLeaveSeat));
        given(studentScheduleRepository.findAllByFixedLeaveSeatAndDay(fixedLeaveSeat, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule1, studentSchedule2));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any()))
                .willReturn(0);
        given(fixedLeaveSeatStudentRepository.findAllByFixedLeaveSeat(fixedLeaveSeat))
                .willReturn(List.of(student1, student2));

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: LeaveSeat 엔티티 저장 횟수를 검증한다
        ArgumentCaptor<LeaveSeatEntity> leaveSeatCaptor = ArgumentCaptor.forClass(LeaveSeatEntity.class);
        verify(leaveSeatRepository, atLeastOnce()).save(leaveSeatCaptor.capture());

        List<LeaveSeatEntity> savedLeaveSeats = leaveSeatCaptor.getAllValues();

        // 🐛 BUG: 현재 코드는 각 학생마다 LeaveSeat을 생성합니다.
        // 2명의 학생이 있으면 2개의 LeaveSeat이 생성되는데, 이는 잘못된 동작입니다.
        // FixedLeaveSeat 1개당 LeaveSeat 1개만 생성되어야 합니다.
        //
        // 현재: 2개 생성됨 (student1용 1개 + student2용 1개)
        // 기대: 1개 생성됨 (fixedLeaveSeat용 1개)
        assertThat(savedLeaveSeats)
                .as("FixedLeaveSeat 1개당 LeaveSeat은 1개만 생성되어야 합니다")
                .hasSize(1);  // ❌ 현재는 2개 생성됨
    }

    @Test
    @DisplayName("BUG: 동일한 LeaveSeat이 여러 LeaveSeatSchedule에 공유되어야 한다")
    void shouldShareSameLeaveSeatAcrossMultipleLeaveSeatSchedules() {
        // Given: 고정 이석에 3명의 학생이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(WeekDay.MON.toDayOfWeek());

        TeacherEntity teacher = createMockTeacher(1L);
        PlaceEntity place = createMockPlace(1L, "도서관");
        FixedLeaveSeatEntity fixedLeaveSeat = createMockFixedLeaveSeat(1L, teacher, place, WeekDay.MON, SchoolPeriod.SEVEN_PERIOD, "특별활동");

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);
        StudentEntity student3 = createMockStudent(3L, 1, 3);

        StudentScheduleEntity studentSchedule1 = createMockStudentSchedule(1L, student1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule2 = createMockStudentSchedule(2L, student2, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule3 = createMockStudentSchedule(3L, student3, nextMonday, SchoolPeriod.SEVEN_PERIOD);

        given(fixedLeaveSeatRepository.findAll())
                .willReturn(List.of(fixedLeaveSeat));
        given(studentScheduleRepository.findAllByFixedLeaveSeatAndDay(fixedLeaveSeat, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule1, studentSchedule2, studentSchedule3));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any()))
                .willReturn(0);
        given(fixedLeaveSeatStudentRepository.findAllByFixedLeaveSeat(fixedLeaveSeat))
                .willReturn(List.of(student1, student2, student3));

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 저장된 LeaveSeatSchedule들을 검증한다
        ArgumentCaptor<LeaveSeatScheduleEntity> scheduleCaptor = ArgumentCaptor.forClass(LeaveSeatScheduleEntity.class);
        verify(leaveSeatScheduleRepository, times(3)).save(scheduleCaptor.capture());

        List<LeaveSeatScheduleEntity> savedSchedules = scheduleCaptor.getAllValues();

        // 🐛 BUG: 현재 코드는 각 학생마다 새로운 LeaveSeat을 생성하므로,
        // 3개의 LeaveSeatSchedule이 각각 다른 LeaveSeat을 참조합니다.
        // 하지만 올바른 동작은 모든 LeaveSeatSchedule이 동일한 LeaveSeat을 참조해야 합니다.
        //
        // 현재: schedule1.leaveSeat != schedule2.leaveSeat != schedule3.leaveSeat
        // 기대: schedule1.leaveSeat == schedule2.leaveSeat == schedule3.leaveSeat
        LeaveSeatEntity firstLeaveSeat = savedSchedules.get(0).getLeaveSeat();

        assertThat(savedSchedules)
                .as("모든 LeaveSeatSchedule은 동일한 LeaveSeat을 참조해야 합니다")
                .extracting(LeaveSeatScheduleEntity::getLeaveSeat)
                .containsOnly(firstLeaveSeat);  // ❌ 현재는 서로 다른 LeaveSeat을 참조함
    }

    @Test
    @DisplayName("여러 고정 이석이 있을 때 각각 독립적으로 처리되어야 한다")
    void shouldHandleMultipleFixedLeaveSeatsIndependently() {
        // Given: 2개의 고정 이석이 있을 때
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusWeeks(1);
        LocalDate nextMonday = nextWeek.with(WeekDay.MON.toDayOfWeek());
        LocalDate nextTuesday = nextWeek.with(WeekDay.TUE.toDayOfWeek());

        TeacherEntity teacher = createMockTeacher(1L);
        PlaceEntity place1 = createMockPlace(1L, "도서관");
        PlaceEntity place2 = createMockPlace(2L, "컴퓨터실");

        FixedLeaveSeatEntity fixedLeaveSeat1 = createMockFixedLeaveSeat(1L, teacher, place1, WeekDay.MON, SchoolPeriod.SEVEN_PERIOD, "특별활동1");
        FixedLeaveSeatEntity fixedLeaveSeat2 = createMockFixedLeaveSeat(2L, teacher, place2, WeekDay.TUE, SchoolPeriod.SEVEN_PERIOD, "특별활동2");

        StudentEntity student1 = createMockStudent(1L, 1, 1);
        StudentEntity student2 = createMockStudent(2L, 1, 2);

        StudentScheduleEntity studentSchedule1 = createMockStudentSchedule(1L, student1, nextMonday, SchoolPeriod.SEVEN_PERIOD);
        StudentScheduleEntity studentSchedule2 = createMockStudentSchedule(2L, student2, nextTuesday, SchoolPeriod.SEVEN_PERIOD);

        given(fixedLeaveSeatRepository.findAll())
                .willReturn(List.of(fixedLeaveSeat1, fixedLeaveSeat2));
        given(studentScheduleRepository.findAllByFixedLeaveSeatAndDay(fixedLeaveSeat1, nextMonday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule1));
        given(studentScheduleRepository.findAllByFixedLeaveSeatAndDay(fixedLeaveSeat2, nextTuesday, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule2));
        given(scheduleRepository.findLastStackOrderByStudentScheduleId(any()))
                .willReturn(0);
        given(fixedLeaveSeatStudentRepository.findAllByFixedLeaveSeat(any()))
                .willReturn(List.of());

        // When: 스케줄을 설정하면
        strategy.settingSchedule(nextWeek);

        // Then: 2개의 이석 스케줄이 생성되어야 한다
        verify(leaveSeatScheduleRepository, times(2)).save(any(LeaveSeatScheduleEntity.class));
    }

    private TeacherEntity createMockTeacher(Long id) {
        TeacherEntity teacher = mock(TeacherEntity.class);
        given(teacher.getId()).willReturn(id);
        given(teacher.hasStudentScheduleChangeAuthority()).willReturn(true);
        return teacher;
    }

    private PlaceEntity createMockPlace(Long id, String name) {
        PlaceEntity place = mock(PlaceEntity.class);
        given(place.getId()).willReturn(id);
        given(place.getName()).willReturn(name);
        return place;
    }

    private FixedLeaveSeatEntity createMockFixedLeaveSeat(Long id, TeacherEntity teacher, PlaceEntity place, WeekDay weekDay, SchoolPeriod period, String cause) {
        FixedLeaveSeatEntity fixedLeaveSeat = mock(FixedLeaveSeatEntity.class);
        given(fixedLeaveSeat.getId()).willReturn(id);
        given(fixedLeaveSeat.getTeacher()).willReturn(teacher);
        given(fixedLeaveSeat.getPlace()).willReturn(place);
        given(fixedLeaveSeat.getWeekDay()).willReturn(weekDay);
        given(fixedLeaveSeat.getPeriod()).willReturn(period);
        given(fixedLeaveSeat.getCause()).willReturn(cause);
        return fixedLeaveSeat;
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
