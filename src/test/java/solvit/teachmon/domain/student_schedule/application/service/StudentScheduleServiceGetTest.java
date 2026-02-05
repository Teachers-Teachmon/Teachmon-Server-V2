package solvit.teachmon.domain.student_schedule.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.application.mapper.StudentScheduleMapper;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.StudentScheduleResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 스케줄 서비스 - 학년별 학생 스케줄 조회 테스트")
class StudentScheduleServiceGetTest {

    @Mock
    private StudentScheduleMapper studentScheduleMapper;

    @Mock
    private StudentScheduleRepository studentScheduleRepository;

    @InjectMocks
    private StudentScheduleService studentScheduleService;

    @Test
    @DisplayName("학년별 학생 스케줄을 조회할 수 있다")
    void shouldGetGradeStudentSchedules() {
        // Given: 학년, 날짜, 교시 정보가 있을 때
        Integer grade = 1;
        LocalDate day = LocalDate.of(2024, 1, 15);
        SchoolPeriod period = SchoolPeriod.ONE_PERIOD;

        StudentScheduleDto student1 = new StudentScheduleDto(
                1L, 1, 1, 1, "김학생",
                day, period, 1L, ScheduleType.SELF_STUDY
        );
        StudentScheduleDto student2 = new StudentScheduleDto(
                2L, 1, 1, 2, "이학생",
                day, period, 2L, ScheduleType.AWAY
        );
        StudentScheduleDto student3 = new StudentScheduleDto(
                3L, 1, 2, 1, "박학생",
                day, period, 3L, ScheduleType.SELF_STUDY
        );

        Map<Integer, List<StudentScheduleDto>> mockRepositoryResult = Map.of(
                1, List.of(student1, student2),
                2, List.of(student3)
        );

        ClassStudentScheduleResponse class1Response = ClassStudentScheduleResponse.builder()
                .classNumber(1)
                .students(List.of(
                        StudentScheduleResponse.builder()
                                .studentId(1L)
                                .number(1)
                                .name("김학생")
                                .state(ScheduleType.SELF_STUDY)
                                .scheduleId(1L)
                                .build(),
                        StudentScheduleResponse.builder()
                                .studentId(2L)
                                .number(2)
                                .name("이학생")
                                .state(ScheduleType.AWAY)
                                .scheduleId(2L)
                                .build()
                ))
                .build();

        ClassStudentScheduleResponse class2Response = ClassStudentScheduleResponse.builder()
                .classNumber(2)
                .students(List.of(
                        StudentScheduleResponse.builder()
                                .studentId(3L)
                                .number(1)
                                .name("박학생")
                                .state(ScheduleType.SELF_STUDY)
                                .scheduleId(3L)
                                .build()
                ))
                .build();

        given(studentScheduleRepository.findByGradeAndPeriodGroupByClass(grade, day, period))
                .willReturn(mockRepositoryResult);
        given(studentScheduleMapper.toResponse(1, List.of(student1, student2)))
                .willReturn(class1Response);
        given(studentScheduleMapper.toResponse(2, List.of(student3)))
                .willReturn(class2Response);

        // When: 학년별 학생 스케줄을 조회하면
        List<ClassStudentScheduleResponse> result = studentScheduleService.getGradeStudentSchedules(grade, day, period);

        // Then: 반별로 그룹화된 학생 스케줄이 반환된다
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ClassStudentScheduleResponse::classNumber)
                .containsExactlyInAnyOrder(1, 2);

        verify(studentScheduleRepository, times(1))
                .findByGradeAndPeriodGroupByClass(grade, day, period);
        verify(studentScheduleMapper, times(1))
                .toResponse(1, List.of(student1, student2));
        verify(studentScheduleMapper, times(1))
                .toResponse(2, List.of(student3));
    }

    @Test
    @DisplayName("해당 학년에 학생이 없으면 빈 리스트가 반환된다")
    void shouldReturnEmptyListWhenNoStudents() {
        // Given: 학생이 없는 학년 정보가 있을 때
        Integer grade = 3;
        LocalDate day = LocalDate.of(2024, 1, 15);
        SchoolPeriod period = SchoolPeriod.ONE_PERIOD;

        given(studentScheduleRepository.findByGradeAndPeriodGroupByClass(grade, day, period))
                .willReturn(Map.of());

        // When: 학년별 학생 스케줄을 조회하면
        List<ClassStudentScheduleResponse> result = studentScheduleService.getGradeStudentSchedules(grade, day, period);

        // Then: 빈 리스트가 반환된다
        assertThat(result).isEmpty();

        verify(studentScheduleRepository, times(1))
                .findByGradeAndPeriodGroupByClass(grade, day, period);
    }

    @Test
    @DisplayName("여러 반의 학생 스케줄을 한 번에 조회할 수 있다")
    void shouldGetMultipleClassSchedules() {
        // Given: 여러 반에 학생들이 있을 때
        Integer grade = 2;
        LocalDate day = LocalDate.of(2024, 1, 15);
        SchoolPeriod period = SchoolPeriod.TWO_PERIOD;

        StudentScheduleDto class1Student1 = new StudentScheduleDto(
                1L, 2, 1, 1, "김학생", day, period, 1L, ScheduleType.SELF_STUDY
        );
        StudentScheduleDto class2Student1 = new StudentScheduleDto(
                2L, 2, 2, 1, "이학생", day, period, 2L, ScheduleType.EXIT
        );
        StudentScheduleDto class3Student1 = new StudentScheduleDto(
                3L, 2, 3, 1, "박학생", day, period, 3L, ScheduleType.AFTER_SCHOOL
        );

        Map<Integer, List<StudentScheduleDto>> mockRepositoryResult = Map.of(
                1, List.of(class1Student1),
                2, List.of(class2Student1),
                3, List.of(class3Student1)
        );

        ClassStudentScheduleResponse class1Response = ClassStudentScheduleResponse.builder()
                .classNumber(1)
                .students(List.of(StudentScheduleResponse.builder()
                        .studentId(1L).number(1).name("김학생")
                        .state(ScheduleType.SELF_STUDY).scheduleId(1L).build()))
                .build();
        ClassStudentScheduleResponse class2Response = ClassStudentScheduleResponse.builder()
                .classNumber(2)
                .students(List.of(StudentScheduleResponse.builder()
                        .studentId(2L).number(1).name("이학생")
                        .state(ScheduleType.EXIT).scheduleId(2L).build()))
                .build();
        ClassStudentScheduleResponse class3Response = ClassStudentScheduleResponse.builder()
                .classNumber(3)
                .students(List.of(StudentScheduleResponse.builder()
                        .studentId(3L).number(1).name("박학생")
                        .state(ScheduleType.AFTER_SCHOOL).scheduleId(3L).build()))
                .build();

        given(studentScheduleRepository.findByGradeAndPeriodGroupByClass(grade, day, period))
                .willReturn(mockRepositoryResult);
        given(studentScheduleMapper.toResponse(1, List.of(class1Student1))).willReturn(class1Response);
        given(studentScheduleMapper.toResponse(2, List.of(class2Student1))).willReturn(class2Response);
        given(studentScheduleMapper.toResponse(3, List.of(class3Student1))).willReturn(class3Response);

        // When: 학년별 학생 스케줄을 조회하면
        List<ClassStudentScheduleResponse> result = studentScheduleService.getGradeStudentSchedules(grade, day, period);

        // Then: 3개 반의 스케줄이 모두 조회된다
        assertThat(result).hasSize(3);
        assertThat(result).extracting(ClassStudentScheduleResponse::classNumber)
                .containsExactlyInAnyOrder(1, 2, 3);

        verify(studentScheduleRepository, times(1))
                .findByGradeAndPeriodGroupByClass(grade, day, period);
    }
}
