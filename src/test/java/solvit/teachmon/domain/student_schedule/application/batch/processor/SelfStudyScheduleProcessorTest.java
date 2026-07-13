package solvit.teachmon.domain.student_schedule.application.batch.processor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.student_schedule.application.batch.dto.SelfStudyScheduleDto;
import solvit.teachmon.domain.student_schedule.application.batch.support.BatchTestFixtures;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("SelfStudyScheduleProcessor 테스트")
class SelfStudyScheduleProcessorTest {

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private SelfStudyScheduleProcessor processor;

    @Test
    @DisplayName("ExecutionContext의 maxStackOrderMap 값을 사용해 SELF_STUDY 스케줄을 생성한다")
    void shouldCreateSelfStudyScheduleUsingExecutionContextStackOrder() throws Exception {
        LocalDate baseDate = LocalDate.of(2026, 4, 6);
        BatchTestFixtures.setField(processor, "baseDate", baseDate);
        BatchTestFixtures.setField(processor, "maxStackOrderMap", Map.of(10L, 2));

        SelfStudyEntity selfStudy = mock(SelfStudyEntity.class);
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        StudentEntity student = mock(StudentEntity.class);
        PlaceEntity place = mock(PlaceEntity.class);

        given(selfStudy.getWeekDay()).willReturn(WeekDay.MON);
        given(selfStudy.getGrade()).willReturn(1);
        given(selfStudy.getPeriod()).willReturn(SchoolPeriod.SEVEN_PERIOD);

        given(studentSchedule.getId()).willReturn(10L);
        given(studentSchedule.getStudent()).willReturn(student);
        given(studentSchedule.getDay()).willReturn(baseDate);
        given(studentSchedule.getPeriod()).willReturn(SchoolPeriod.SEVEN_PERIOD);
        given(student.getGrade()).willReturn(1);
        given(student.getClassNumber()).willReturn(1);

        given(studentScheduleRepository.findAllByGradeAndDayAndPeriod(1, baseDate, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule));
        given(placeRepository.findAllByGradePrefix(1)).willReturn(Map.of(1, place));
        given(placeRepository.checkPlaceAvailability(baseDate, SchoolPeriod.SEVEN_PERIOD, place)).willReturn(false);

        List<SelfStudyScheduleDto> result = processor.process(selfStudy);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().schedule().getType()).isEqualTo(ScheduleType.SELF_STUDY);
        assertThat(result.getFirst().schedule().getStackOrder()).isEqualTo(3);
        assertThat(result.getFirst().selfStudySchedule().getPlace()).isSameAs(place);
    }

    @Test
    @DisplayName("maxStackOrderMap에 값이 없으면 stackOrder를 1로 시작한다")
    void shouldStartStackOrderFromOneWhenNoPreviousStack() throws Exception {
        LocalDate baseDate = LocalDate.of(2026, 4, 6);
        BatchTestFixtures.setField(processor, "baseDate", baseDate);
        BatchTestFixtures.setField(processor, "maxStackOrderMap", Map.of());

        SelfStudyEntity selfStudy = mock(SelfStudyEntity.class);
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        StudentEntity student = mock(StudentEntity.class);
        PlaceEntity place = mock(PlaceEntity.class);

        given(selfStudy.getWeekDay()).willReturn(WeekDay.MON);
        given(selfStudy.getGrade()).willReturn(1);
        given(selfStudy.getPeriod()).willReturn(SchoolPeriod.SEVEN_PERIOD);

        given(studentSchedule.getId()).willReturn(99L);
        given(studentSchedule.getStudent()).willReturn(student);
        given(studentSchedule.getDay()).willReturn(baseDate);
        given(studentSchedule.getPeriod()).willReturn(SchoolPeriod.SEVEN_PERIOD);
        given(student.getGrade()).willReturn(1);
        given(student.getClassNumber()).willReturn(1);

        given(studentScheduleRepository.findAllByGradeAndDayAndPeriod(1, baseDate, SchoolPeriod.SEVEN_PERIOD))
                .willReturn(List.of(studentSchedule));
        given(placeRepository.findAllByGradePrefix(1)).willReturn(Map.of(1, place));
        given(placeRepository.checkPlaceAvailability(baseDate, SchoolPeriod.SEVEN_PERIOD, place)).willReturn(false);

        List<SelfStudyScheduleDto> result = processor.process(selfStudy);

        assertThat(result.getFirst().schedule().getStackOrder()).isEqualTo(1);
    }
}

