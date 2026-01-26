package solvit.teachmon.domain.student_schedule.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PeriodScheduleDto;
import solvit.teachmon.domain.student_schedule.application.mapper.StudentScheduleMapper;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.HistoryStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PeriodScheduleResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 스케줄 서비스 - 학생 스케줄 히스토리 조회 테스트")
class StudentScheduleServiceGetHistoryTest {

    @Mock
    private StudentScheduleMapper studentScheduleMapper;

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @InjectMocks
    private StudentScheduleService studentScheduleService;

    @Test
    @DisplayName("학생 번호로 학생 스케줄 히스토리를 조회할 수 있다")
    void shouldGetStudentScheduleHistoryByStudentNumber() {
        // Given: 학생 번호와 날짜가 주어졌을 때
        String query = "2115";
        LocalDate day = LocalDate.of(2026, 1, 1);

        StudentEntity student = StudentEntity.builder()
                .grade(2)
                .classNumber(1)
                .number(15)
                .name("허온")
                .build();

        List<PeriodScheduleDto> schedules = List.of(
                new PeriodScheduleDto(1L, SchoolPeriod.SEVEN_PERIOD, ScheduleType.SELF_STUDY),
                new PeriodScheduleDto(2L, SchoolPeriod.EIGHT_AND_NINE_PERIOD, ScheduleType.SELF_STUDY),
                new PeriodScheduleDto(3L, SchoolPeriod.TEN_AND_ELEVEN_PERIOD, ScheduleType.AFTER_SCHOOL)
        );

        Map<StudentEntity, List<PeriodScheduleDto>> mockRepositoryResult = Map.of(student, schedules);

        HistoryStudentScheduleResponse expectedResponse = HistoryStudentScheduleResponse.builder()
                .studentNumber(2115)
                .name("허온")
                .onePeriod(null)
                .twoPeriod(null)
                .threePeriod(null)
                .fourPeriod(null)
                .fivePeriod(null)
                .sixPeriod(null)
                .sevenPeriod(PeriodScheduleResponse.builder().scheduleId(1L).state(ScheduleType.SELF_STUDY).build())
                .eightAndNinePeriod(PeriodScheduleResponse.builder().scheduleId(2L).state(ScheduleType.SELF_STUDY).build())
                .tenAndElevenPeriod(PeriodScheduleResponse.builder().scheduleId(3L).state(ScheduleType.AFTER_SCHOOL).build())
                .build();

        given(studentScheduleRepository.findByQueryAndDayGroupByStudent(query, day))
                .willReturn(mockRepositoryResult);
        given(studentScheduleMapper.toHistoryResponse(student, schedules))
                .willReturn(expectedResponse);

        // When: 학생 스케줄 히스토리를 조회하면
        List<HistoryStudentScheduleResponse> results = studentScheduleService.getStudentScheduleHistory(query, day);

        // Then: 학생의 모든 교시별 스케줄이 반환된다
        assertThat(results).hasSize(1);

        HistoryStudentScheduleResponse response = results.get(0);
        assertThat(response.studentNumber()).isEqualTo(2115);
        assertThat(response.name()).isEqualTo("허온");
        assertThat(response.sevenPeriod().scheduleId()).isEqualTo(1L);
        assertThat(response.sevenPeriod().state()).isEqualTo(ScheduleType.SELF_STUDY);
        assertThat(response.eightAndNinePeriod().scheduleId()).isEqualTo(2L);
        assertThat(response.eightAndNinePeriod().state()).isEqualTo(ScheduleType.SELF_STUDY);
        assertThat(response.tenAndElevenPeriod().scheduleId()).isEqualTo(3L);
        assertThat(response.tenAndElevenPeriod().state()).isEqualTo(ScheduleType.AFTER_SCHOOL);

        verify(studentScheduleRepository, times(1))
                .findByQueryAndDayGroupByStudent(query, day);
        verify(studentScheduleMapper, times(1))
                .toHistoryResponse(student, schedules);
    }

    @Test
    @DisplayName("이름으로 여러 학생의 스케줄 히스토리를 조회할 수 있다")
    void shouldGetMultipleStudentScheduleHistoriesByName() {
        // Given: 이름으로 검색할 때
        String query = "허";
        LocalDate day = LocalDate.of(2026, 1, 1);

        StudentEntity student1 = StudentEntity.builder()
                .grade(2).classNumber(1).number(15).name("허온").build();
        StudentEntity student2 = StudentEntity.builder()
                .grade(2).classNumber(1).number(16).name("허준").build();

        List<PeriodScheduleDto> schedules1 = List.of(
                new PeriodScheduleDto(1L, SchoolPeriod.EIGHT_AND_NINE_PERIOD, ScheduleType.SELF_STUDY)
        );
        List<PeriodScheduleDto> schedules2 = List.of(
                new PeriodScheduleDto(2L, SchoolPeriod.EIGHT_AND_NINE_PERIOD, ScheduleType.AWAY)
        );

        Map<StudentEntity, List<PeriodScheduleDto>> mockRepositoryResult = Map.of(
                student1, schedules1,
                student2, schedules2
        );

        HistoryStudentScheduleResponse response1 = HistoryStudentScheduleResponse.builder()
                .studentNumber(2115).name("허온")
                .eightAndNinePeriod(PeriodScheduleResponse.builder().scheduleId(1L).state(ScheduleType.SELF_STUDY).build()).build();
        HistoryStudentScheduleResponse response2 = HistoryStudentScheduleResponse.builder()
                .studentNumber(2116).name("허준")
                .eightAndNinePeriod(PeriodScheduleResponse.builder().scheduleId(2L).state(ScheduleType.AWAY).build()).build();

        given(studentScheduleRepository.findByQueryAndDayGroupByStudent(query, day))
                .willReturn(mockRepositoryResult);
        given(studentScheduleMapper.toHistoryResponse(student1, schedules1)).willReturn(response1);
        given(studentScheduleMapper.toHistoryResponse(student2, schedules2)).willReturn(response2);

        // When: 학생 스케줄 히스토리를 조회하면
        List<HistoryStudentScheduleResponse> results = studentScheduleService.getStudentScheduleHistory(query, day);

        // Then: 이름에 "허"가 포함된 모든 학생이 반환된다
        assertThat(results).hasSize(2);
        assertThat(results).extracting(HistoryStudentScheduleResponse::name)
                .containsExactlyInAnyOrder("허온", "허준");

        verify(studentScheduleRepository, times(1))
                .findByQueryAndDayGroupByStudent(query, day);
    }

    @Test
    @DisplayName("스케줄이 없는 경우 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoSchedules() {
        // Given: 검색어와 날짜가 주어졌지만 스케줄이 없을 때
        String query = "3105";
        LocalDate day = LocalDate.of(2026, 1, 1);

        given(studentScheduleRepository.findByQueryAndDayGroupByStudent(query, day))
                .willReturn(Map.of());

        // When: 학생 스케줄 히스토리를 조회하면
        List<HistoryStudentScheduleResponse> results = studentScheduleService.getStudentScheduleHistory(query, day);

        // Then: 빈 리스트가 반환된다
        assertThat(results).isEmpty();

        verify(studentScheduleRepository, times(1))
                .findByQueryAndDayGroupByStudent(query, day);
        verify(studentScheduleMapper, never()).toHistoryResponse(any(), any());
    }

    @Test
    @DisplayName("일부 교시만 있는 학생도 조회할 수 있다")
    void shouldGetStudentScheduleHistoryWithPartialPeriods() {
        // Given: 일부 교시만 있는 학생의 스케줄이 있을 때
        String query = "1210";
        LocalDate day = LocalDate.of(2026, 1, 1);

        StudentEntity student = StudentEntity.builder()
                .grade(1).classNumber(2).number(10).name("김철수").build();

        List<PeriodScheduleDto> schedules = List.of(
                new PeriodScheduleDto(1L, SchoolPeriod.EIGHT_AND_NINE_PERIOD, ScheduleType.EXIT)
        );

        Map<StudentEntity, List<PeriodScheduleDto>> mockRepositoryResult = Map.of(student, schedules);

        HistoryStudentScheduleResponse expectedResponse = HistoryStudentScheduleResponse.builder()
                .studentNumber(1210)
                .name("김철수")
                .onePeriod(null)
                .twoPeriod(null)
                .threePeriod(null)
                .fourPeriod(null)
                .fivePeriod(null)
                .sixPeriod(null)
                .sevenPeriod(null)
                .eightAndNinePeriod(PeriodScheduleResponse.builder().scheduleId(1L).state(ScheduleType.EXIT).build())
                .tenAndElevenPeriod(null)
                .build();

        given(studentScheduleRepository.findByQueryAndDayGroupByStudent(query, day))
                .willReturn(mockRepositoryResult);
        given(studentScheduleMapper.toHistoryResponse(student, schedules))
                .willReturn(expectedResponse);

        // When: 학생 스케줄 히스토리를 조회하면
        List<HistoryStudentScheduleResponse> results = studentScheduleService.getStudentScheduleHistory(query, day);

        // Then: 일부 교시만 값이 있고 나머지는 null이다
        assertThat(results).hasSize(1);

        HistoryStudentScheduleResponse response = results.get(0);
        assertThat(response.studentNumber()).isEqualTo(1210);
        assertThat(response.name()).isEqualTo("김철수");
        assertThat(response.sevenPeriod()).isNull();
        assertThat(response.eightAndNinePeriod().scheduleId()).isEqualTo(1L);
        assertThat(response.eightAndNinePeriod().state()).isEqualTo(ScheduleType.EXIT);
        assertThat(response.tenAndElevenPeriod()).isNull();

        verify(studentScheduleRepository, times(1))
                .findByQueryAndDayGroupByStudent(query, day);
        verify(studentScheduleMapper, times(1))
                .toHistoryResponse(student, schedules);
    }

    @Test
    @DisplayName("빈 쿼리로 조회할 수 있다")
    void shouldGetStudentScheduleHistoryWithEmptyQuery() {
        // Given: 빈 쿼리와 날짜가 주어졌을 때
        String query = "";
        LocalDate day = LocalDate.of(2026, 1, 1);

        StudentEntity student1 = StudentEntity.builder()
                .grade(1).classNumber(1).number(1).name("학생1").build();
        StudentEntity student2 = StudentEntity.builder()
                .grade(1).classNumber(1).number(2).name("학생2").build();

        List<PeriodScheduleDto> schedules1 = List.of(
                new PeriodScheduleDto(1L, SchoolPeriod.ONE_PERIOD, ScheduleType.SELF_STUDY)
        );
        List<PeriodScheduleDto> schedules2 = List.of(
                new PeriodScheduleDto(2L, SchoolPeriod.ONE_PERIOD, ScheduleType.AWAY)
        );

        Map<StudentEntity, List<PeriodScheduleDto>> mockRepositoryResult = Map.of(
                student1, schedules1,
                student2, schedules2
        );

        HistoryStudentScheduleResponse response1 = HistoryStudentScheduleResponse.builder()
                .studentNumber(1101).name("학생1")
                .onePeriod(PeriodScheduleResponse.builder().scheduleId(1L).state(ScheduleType.SELF_STUDY).build()).build();
        HistoryStudentScheduleResponse response2 = HistoryStudentScheduleResponse.builder()
                .studentNumber(1102).name("학생2")
                .onePeriod(PeriodScheduleResponse.builder().scheduleId(2L).state(ScheduleType.AWAY).build()).build();

        given(studentScheduleRepository.findByQueryAndDayGroupByStudent(query, day))
                .willReturn(mockRepositoryResult);
        given(studentScheduleMapper.toHistoryResponse(student1, schedules1)).willReturn(response1);
        given(studentScheduleMapper.toHistoryResponse(student2, schedules2)).willReturn(response2);

        // When: 학생 스케줄 히스토리를 조회하면
        List<HistoryStudentScheduleResponse> results = studentScheduleService.getStudentScheduleHistory(query, day);

        // Then: 모든 학생의 스케줄이 반환된다
        assertThat(results).hasSize(2);

        verify(studentScheduleRepository, times(1))
                .findByQueryAndDayGroupByStudent(query, day);
    }
}
