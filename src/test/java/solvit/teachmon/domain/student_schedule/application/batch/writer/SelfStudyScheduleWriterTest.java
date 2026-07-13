package solvit.teachmon.domain.student_schedule.application.batch.writer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.Chunk;
import solvit.teachmon.domain.student_schedule.application.batch.dto.SelfStudyScheduleDto;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.SelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.SelfStudyScheduleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SelfStudyScheduleWriter 테스트")
class SelfStudyScheduleWriterTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private SelfStudyScheduleRepository selfStudyScheduleRepository;

    @InjectMocks
    private SelfStudyScheduleWriter writer;

    @Test
    @DisplayName("기존 SELF_STUDY 스케줄이 있으면 삭제 후 저장한다")
    void shouldDeleteExistingSelfStudyAndSave() throws Exception {
        StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
        ScheduleEntity newSchedule = mock(ScheduleEntity.class);
        SelfStudyScheduleEntity detail = mock(SelfStudyScheduleEntity.class);
        ScheduleEntity existingSchedule = mock(ScheduleEntity.class);

        given(studentSchedule.getId()).willReturn(10L);
        given(newSchedule.getStudentSchedule()).willReturn(studentSchedule);
        given(scheduleRepository.findByStudentScheduleIdAndType(10L, ScheduleType.SELF_STUDY))
                .willReturn(Optional.of(existingSchedule));

        SelfStudyScheduleDto dto = new SelfStudyScheduleDto(newSchedule, detail);
        Chunk<List<SelfStudyScheduleDto>> chunk = new Chunk<>(List.of(List.of(dto)));

        writer.write(chunk);

        verify(scheduleRepository).delete(existingSchedule);
        verify(scheduleRepository).saveAll(List.of(newSchedule));
        verify(selfStudyScheduleRepository).saveAll(List.of(detail));
    }

    @Test
    @DisplayName("Chunk 경계(101건)에서도 모든 데이터를 저장한다")
    void shouldPersistAllItemsWhenChunkExceedsHundred() throws Exception {
        given(scheduleRepository.findByStudentScheduleIdAndType(anyLong(), eq(ScheduleType.SELF_STUDY)))
                .willReturn(Optional.empty());

        List<SelfStudyScheduleDto> dtos = new ArrayList<>();
        for (long i = 1; i <= 101; i++) {
            StudentScheduleEntity studentSchedule = mock(StudentScheduleEntity.class);
            ScheduleEntity schedule = mock(ScheduleEntity.class);
            SelfStudyScheduleEntity detail = mock(SelfStudyScheduleEntity.class);

            given(studentSchedule.getId()).willReturn(i);
            given(schedule.getStudentSchedule()).willReturn(studentSchedule);

            dtos.add(new SelfStudyScheduleDto(schedule, detail));
        }

        writer.write(new Chunk<>(List.of(dtos)));

        ArgumentCaptor<List<ScheduleEntity>> scheduleCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<SelfStudyScheduleEntity>> detailCaptor = ArgumentCaptor.forClass(List.class);
        verify(scheduleRepository).saveAll(scheduleCaptor.capture());
        verify(selfStudyScheduleRepository).saveAll(detailCaptor.capture());

        assertThat(scheduleCaptor.getValue()).hasSize(101);
        assertThat(detailCaptor.getValue()).hasSize(101);
    }
}

