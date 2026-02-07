package solvit.teachmon.domain.student_schedule.domain.entity.schedules;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.exception.AfterSchoolScheduleValueInvalidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("AfterSchoolScheduleEntity 테스트")
class AfterSchoolScheduleEntityTest {

    @Test
    @DisplayName("유효한 스케줄과 방과후로 엔티티를 생성한다")
    void shouldCreateEntityWhenValid() {
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        given(schedule.getType()).willReturn(ScheduleType.AFTER_SCHOOL);
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);

        AfterSchoolScheduleEntity entity = AfterSchoolScheduleEntity.builder()
                .schedule(schedule)
                .afterSchool(afterSchool)
                .build();

        assertThat(entity.getSchedule()).isEqualTo(schedule);
        assertThat(entity.getAfterSchool()).isEqualTo(afterSchool);
    }

    @Test
    @DisplayName("스케줄이 null이면 예외가 발생한다")
    void shouldThrowWhenScheduleIsNull() {
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);

        assertThatThrownBy(() -> AfterSchoolScheduleEntity.builder()
                .schedule(null)
                .afterSchool(afterSchool)
                .build())
                .isInstanceOf(AfterSchoolScheduleValueInvalidException.class)
                .hasMessageContaining("schedule");
    }

    @Test
    @DisplayName("스케줄 타입이 방과후가 아니면 예외가 발생한다")
    void shouldThrowWhenScheduleTypeInvalid() {
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        given(schedule.getType()).willReturn(ScheduleType.EXIT);
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);

        assertThatThrownBy(() -> AfterSchoolScheduleEntity.builder()
                .schedule(schedule)
                .afterSchool(afterSchool)
                .build())
                .isInstanceOf(AfterSchoolScheduleValueInvalidException.class)
                .hasMessageContaining("타입");
    }

    @Test
    @DisplayName("방과후가 null이면 예외가 발생한다")
    void shouldThrowWhenAfterSchoolIsNull() {
        ScheduleEntity schedule = mock(ScheduleEntity.class);
        given(schedule.getType()).willReturn(ScheduleType.AFTER_SCHOOL);

        assertThatThrownBy(() -> AfterSchoolScheduleEntity.builder()
                .schedule(schedule)
                .afterSchool(null)
                .build())
                .isInstanceOf(AfterSchoolScheduleValueInvalidException.class)
                .hasMessageContaining("afterSchool");
    }
}
