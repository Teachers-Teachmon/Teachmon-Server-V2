package solvit.teachmon.domain.after_school.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.service.AfterSchoolStudentDomainService;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolInfoException;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolUpdateRequestDto;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AfterSchoolScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("방과후 수정 분기 테스트")
class AfterSchoolServiceUpdateFlowTest {

    @Mock
    private AfterSchoolStudentDomainService afterSchoolStudentDomainService;
    @Mock
    private SupervisionBanDayRepository supervisionBanDayRepository;
    @Mock
    private AfterSchoolRepository afterSchoolRepository;
    @Mock
    private AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;
    @Mock
    private AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private StudentScheduleRepository studentScheduleRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private AfterSchoolScheduleService afterSchoolScheduleService;
    @Mock
    private AfterSchoolScheduleRepository afterSchoolScheduleRepository;

    private AfterSchoolService afterSchoolService;

    @BeforeEach
    void setUp() {
        afterSchoolService = new AfterSchoolService(
                afterSchoolStudentDomainService,
                supervisionBanDayRepository,
                afterSchoolRepository,
                afterSchoolBusinessTripRepository,
                afterSchoolReinforcementRepository,
                teacherRepository,
                studentRepository,
                branchRepository,
                placeRepository,
                studentScheduleRepository,
                scheduleRepository,
                afterSchoolScheduleService,
                afterSchoolScheduleRepository
        );
    }

    @Test
    @DisplayName("단일 ID 수정은 기존 로직대로 처리한다")
    void updateSingleAfterSchool() {
        TeacherEntity originalTeacher = mockTeacher(10L);
        TeacherEntity newTeacher = mockTeacher(20L);
        PlaceEntity currentPlace = mockPlace(30L);
        PlaceEntity newPlace = mockPlace(40L);
        AfterSchoolEntity afterSchool = mockAfterSchool(originalTeacher, currentPlace, WeekDay.MON, SchoolPeriod.EIGHT_AND_NINE_PERIOD, 2026, "기존 방과후", 2);

        given(afterSchoolRepository.findWithAllRelations(1L)).willReturn(Optional.of(afterSchool));
        given(teacherRepository.findById(20L)).willReturn(Optional.of(newTeacher));
        given(placeRepository.findById(40L)).willReturn(Optional.of(newPlace));

        AfterSchoolUpdateRequestDto request = new AfterSchoolUpdateRequestDto(
                "1",
                2026,
                2,
                WeekDay.TUE,
                "EIGHT_AND_NINE_PERIOD",
                20L,
                40L,
                "수정 방과후",
                null
        );

        afterSchoolService.updateAfterSchool(request);

        verify(afterSchool).updateAfterSchool(
                newTeacher,
                newPlace,
                WeekDay.TUE,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                2026,
                "수정 방과후",
                2
        );
    }

    @Test
    @DisplayName("병합 ID와 8~11교시 요청이면 두 방과후를 각각 수정한다")
    void updateCombinedAfterSchoolToCombinedPeriod() {
        TeacherEntity teacher = mockTeacher(10L);
        PlaceEntity place = mockPlace(30L);
        AfterSchoolEntity eightNine = mockAfterSchool(teacher, place, WeekDay.MON, SchoolPeriod.EIGHT_AND_NINE_PERIOD, 2026, "방과후", 2);
        AfterSchoolEntity tenEleven = mockAfterSchool(teacher, place, WeekDay.MON, SchoolPeriod.TEN_AND_ELEVEN_PERIOD, 2026, "방과후", 2);

        given(afterSchoolRepository.findWithAllRelations(1L)).willReturn(Optional.of(eightNine));
        given(afterSchoolRepository.findWithAllRelations(2L)).willReturn(Optional.of(tenEleven));

        AfterSchoolUpdateRequestDto request = new AfterSchoolUpdateRequestDto(
                "1,2",
                2026,
                2,
                WeekDay.MON,
                "EIGHT_TO_ELEVEN_PERIOD",
                null,
                null,
                "통합 방과후",
                null
        );

        afterSchoolService.updateAfterSchool(request);

        verify(eightNine).updateAfterSchool(
                teacher,
                place,
                WeekDay.MON,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                2026,
                "통합 방과후",
                2
        );
        verify(tenEleven).updateAfterSchool(
                teacher,
                place,
                WeekDay.MON,
                SchoolPeriod.TEN_AND_ELEVEN_PERIOD,
                2026,
                "통합 방과후",
                2
        );
        verify(afterSchoolRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("병합 ID를 8~9교시로 축소하면 10~11교시를 먼저 삭제한 뒤 8~9교시를 수정한다")
    void shrinkCombinedAfterSchoolToEightNinePeriod() {
        TeacherEntity teacher = mockTeacher(10L);
        PlaceEntity place = mockPlace(30L);
        AfterSchoolEntity eightNine = mockAfterSchool(teacher, place, WeekDay.MON, SchoolPeriod.EIGHT_AND_NINE_PERIOD, 2026, "방과후", 2);
        AfterSchoolEntity tenEleven = mockAfterSchool(teacher, place, WeekDay.MON, SchoolPeriod.TEN_AND_ELEVEN_PERIOD, 2026, "방과후", 2);

        given(afterSchoolRepository.findWithAllRelations(1L)).willReturn(Optional.of(eightNine));
        given(afterSchoolRepository.findById(2L)).willReturn(Optional.of(tenEleven));
        given(afterSchoolScheduleRepository.findScheduleIdsByAfterSchoolId(2L)).willReturn(List.of());

        AfterSchoolUpdateRequestDto request = new AfterSchoolUpdateRequestDto(
                "1,2",
                2026,
                2,
                WeekDay.TUE,
                "EIGHT_AND_NINE_PERIOD",
                null,
                null,
                "축소 방과후",
                null
        );

        afterSchoolService.updateAfterSchool(request);

        InOrder inOrder = inOrder(afterSchoolRepository, eightNine);
        inOrder.verify(afterSchoolRepository).delete(tenEleven);
        inOrder.verify(eightNine).updateAfterSchool(
                teacher,
                place,
                WeekDay.TUE,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                2026,
                "축소 방과후",
                2
        );
    }

    @Test
    @DisplayName("단일 ID에 8~11교시 요청이 들어오면 예외가 발생한다")
    void rejectCombinedPeriodForSingleId() {
        AfterSchoolUpdateRequestDto request = new AfterSchoolUpdateRequestDto(
                "1",
                2026,
                2,
                WeekDay.MON,
                "EIGHT_TO_ELEVEN_PERIOD",
                null,
                null,
                "잘못된 요청",
                null
        );

        assertThatThrownBy(() -> afterSchoolService.updateAfterSchool(request))
                .isInstanceOf(InvalidAfterSchoolInfoException.class)
                .hasMessageContaining("병합된 방과후 ID");

        verify(afterSchoolRepository, never()).findWithAllRelations(anyLong());
    }

    private TeacherEntity mockTeacher(Long id) {
        TeacherEntity teacher = mock(TeacherEntity.class);
        given(teacher.getId()).willReturn(id);
        return teacher;
    }

    private PlaceEntity mockPlace(Long id) {
        PlaceEntity place = mock(PlaceEntity.class);
        given(place.getId()).willReturn(id);
        return place;
    }

    private AfterSchoolEntity mockAfterSchool(
            TeacherEntity teacher,
            PlaceEntity place,
            WeekDay weekDay,
            SchoolPeriod period,
            Integer year,
            String name,
            Integer grade
    ) {
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);
        given(afterSchool.getTeacher()).willReturn(teacher);
        given(afterSchool.getPlace()).willReturn(place);
        given(afterSchool.getWeekDay()).willReturn(weekDay);
        given(afterSchool.getPeriod()).willReturn(period);
        given(afterSchool.getYear()).willReturn(year);
        given(afterSchool.getName()).willReturn(name);
        given(afterSchool.getGrade()).willReturn(grade);
        given(afterSchool.getAfterSchoolStudents()).willReturn(List.of());
        return afterSchool;
    }
}
