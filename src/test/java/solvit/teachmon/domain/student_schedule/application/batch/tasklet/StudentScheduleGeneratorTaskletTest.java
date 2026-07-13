package solvit.teachmon.domain.student_schedule.application.batch.tasklet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.student_schedule.application.batch.support.BatchTestFixtures;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleGenerator;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentScheduleGeneratorTasklet 테스트")
class StudentScheduleGeneratorTaskletTest {

    @Mock
    private StudentScheduleGenerator studentScheduleGenerator;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private StepContribution stepContribution;

    @InjectMocks
    private StudentScheduleGeneratorTasklet tasklet;

    @Test
    @DisplayName("Tasklet 실행 시 빈 틀 생성 후 maxStackOrderMap을 ExecutionContext에 적재한다")
    void shouldCreateStudentSchedulesAndPutMaxStackOrderMap() {
        LocalDate baseDate = LocalDate.of(2026, 4, 6);
        StudentEntity student = mock(StudentEntity.class);

        ScheduleRepository.MaxStackOrderProjection row = mock(ScheduleRepository.MaxStackOrderProjection.class);
        given(row.getStudentScheduleId()).willReturn(101L);
        given(row.getMaxStackOrder()).willReturn(3);

        given(studentRepository.findByYear(2026)).willReturn(List.of(student));
        given(scheduleRepository.findMaxStackOrderGroupByStudentScheduleId()).willReturn(List.of(row));

        ExecutionContext executionContext = new ExecutionContext();
        RepeatStatus status = tasklet.execute(
                stepContribution,
                BatchTestFixtures.chunkContextWithBaseDate(baseDate, executionContext)
        );

        assertThat(status).isEqualTo(RepeatStatus.FINISHED);
        verify(studentScheduleGenerator).deleteFutureStudentSchedules(baseDate);
        verify(studentScheduleGenerator).createStudentScheduleByStudents(List.of(student), baseDate);

        @SuppressWarnings("unchecked")
        Map<Long, Integer> maxStackOrderMap = (Map<Long, Integer>) executionContext.get("maxStackOrderMap");
        assertThat(maxStackOrderMap).containsEntry(101L, 3);
    }
}

