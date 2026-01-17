package solvit.teachmon.domain.management.teacher.application.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.student.domain.exception.InvalidStudentInfoException;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDayEntity;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.WeekDay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 관리 서비스 - 설정 생성 테스트")
class ManagementTeacherFacadeServiceCreateTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SupervisionBanDayRepository supervisionBanDayRepository;

    @InjectMocks
    private ManagementTeacherFacadeService managementTeacherFacadeService;

    @Captor
    private ArgumentCaptor<List<SupervisionBanDayEntity>> banDayEntitiesCaptor;

    @Test
    @DisplayName("선생님의 금지날을 설정할 수 있다")
    void shouldSetTeacherBanDay() {
        // Given: 선생님이 존재하고, 금지날 목록이 있을 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        List<WeekDay> banDays = Arrays.asList(WeekDay.MON, WeekDay.WED, WeekDay.FRI);

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: 금지날을 설정하면
        managementTeacherFacadeService.setTeacherBanDay(teacherId, banDays);

        // Then: 기존 금지날이 삭제되고 새로운 금지날이 저장된다
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(supervisionBanDayRepository, times(1)).deleteAllByTeacherId(teacherId);
        verify(supervisionBanDayRepository, times(1)).saveAll(banDayEntitiesCaptor.capture());

        List<SupervisionBanDayEntity> savedEntities = banDayEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(3);
        assertThat(savedEntities).extracting(SupervisionBanDayEntity::getWeekDay)
                .containsExactlyInAnyOrder(WeekDay.MON, WeekDay.WED, WeekDay.FRI);
        assertThat(savedEntities).allMatch(e -> e.getTeacher().equals(teacher));
    }

    @Test
    @DisplayName("금지날을 빈 리스트로 설정할 수 있다")
    void shouldSetEmptyBanDayList() {
        // Given: 선생님이 존재하고, 빈 금지날 목록이 있을 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        List<WeekDay> banDays = Collections.emptyList();

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: 빈 금지날 목록을 설정하면
        managementTeacherFacadeService.setTeacherBanDay(teacherId, banDays);

        // Then: 기존 금지날이 모두 삭제되고 새로운 금지날은 저장되지 않는다
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(supervisionBanDayRepository, times(1)).deleteAllByTeacherId(teacherId);
        verify(supervisionBanDayRepository, times(1)).saveAll(banDayEntitiesCaptor.capture());

        List<SupervisionBanDayEntity> savedEntities = banDayEntitiesCaptor.getValue();
        assertThat(savedEntities).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 선생님 ID로 금지날 설정 시 예외가 발생한다")
    void shouldThrowExceptionWhenTeacherNotFoundOnSetBanDay() {
        // Given: 존재하지 않는 선생님 ID가 있을 때
        Long teacherId = 999L;
        List<WeekDay> banDays = Arrays.asList(WeekDay.MON, WeekDay.FRI);

        given(teacherRepository.findById(teacherId)).willReturn(Optional.empty());

        // When & Then: 금지날을 설정하면 예외가 발생한다
        assertThatThrownBy(() -> managementTeacherFacadeService.setTeacherBanDay(teacherId, banDays))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(teacherRepository, times(1)).findById(teacherId);
        verify(supervisionBanDayRepository, never()).deleteAllByTeacherId(any());
        verify(supervisionBanDayRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("하나의 금지날만 설정할 수 있다")
    void shouldSetSingleBanDay() {
        // Given: 선생님이 존재하고, 하나의 금지날이 있을 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        List<WeekDay> banDays = Collections.singletonList(WeekDay.TUE);

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: 하나의 금지날을 설정하면
        managementTeacherFacadeService.setTeacherBanDay(teacherId, banDays);

        // Then: 하나의 금지날만 저장된다
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(supervisionBanDayRepository, times(1)).deleteAllByTeacherId(teacherId);
        verify(supervisionBanDayRepository, times(1)).saveAll(banDayEntitiesCaptor.capture());

        List<SupervisionBanDayEntity> savedEntities = banDayEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(1);
        assertThat(savedEntities.getFirst().getWeekDay()).isEqualTo(WeekDay.TUE);
        assertThat(savedEntities.getFirst().getTeacher()).isEqualTo(teacher);
    }

    @Test
    @DisplayName("모든 평일을 금지날로 설정할 수 있다")
    void shouldSetAllWeekdaysAsBanDays() {
        // Given: 선생님이 존재하고, 모든 평일이 금지날로 지정될 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        List<WeekDay> banDays = Arrays.asList(WeekDay.MON, WeekDay.TUE, WeekDay.WED, WeekDay.THU, WeekDay.FRI);

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: 모든 평일을 금지날로 설정하면
        managementTeacherFacadeService.setTeacherBanDay(teacherId, banDays);

        // Then: 5개의 금지날이 저장된다
        verify(teacherRepository, times(1)).findById(teacherId);
        verify(supervisionBanDayRepository, times(1)).deleteAllByTeacherId(teacherId);
        verify(supervisionBanDayRepository, times(1)).saveAll(banDayEntitiesCaptor.capture());

        List<SupervisionBanDayEntity> savedEntities = banDayEntitiesCaptor.getValue();
        assertThat(savedEntities).hasSize(5);
        assertThat(savedEntities).extracting(SupervisionBanDayEntity::getWeekDay)
                .containsExactlyInAnyOrder(WeekDay.MON, WeekDay.TUE, WeekDay.WED, WeekDay.THU, WeekDay.FRI);
    }
}