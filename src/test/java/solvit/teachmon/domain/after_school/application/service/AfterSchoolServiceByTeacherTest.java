package solvit.teachmon.domain.after_school.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.service.AfterSchoolStudentDomainService;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolByTeacherResponseDto;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 선생님별 조회 서비스 테스트")
class AfterSchoolServiceByTeacherTest {

    @InjectMocks
    private AfterSchoolService afterSchoolService;

    @Mock
    private AfterSchoolStudentDomainService afterSchoolStudentDomainService;
    @Mock
    private SupervisionBanDayRepository supervisionBanDayRepository;
    @Mock
    private AfterSchoolRepository afterSchoolRepository;
    @Mock
    private AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;

    @Test
    @DisplayName("선생님 ID로 방과후를 조회할 수 있다")
    void shouldGetAfterSchoolsByTeacherId() {
        // Given
        Long teacherId = 1L;
        
        PlaceEntity place = mock(PlaceEntity.class);
        when(place.getId()).thenReturn(101L);
        when(place.getName()).thenReturn("수학실");
        
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);
        when(afterSchool.getId()).thenReturn(1L);
        when(afterSchool.getName()).thenReturn("수학 방과후");
        when(afterSchool.getWeekDay()).thenReturn(WeekDay.MON);
        when(afterSchool.getPeriod()).thenReturn(SchoolPeriod.SEVEN_PERIOD);
        when(afterSchool.getPlace()).thenReturn(place);
        
        given(afterSchoolRepository.findByTeacherIdWithRelations(teacherId))
                .willReturn(List.of(afterSchool));
        given(afterSchoolReinforcementRepository.findAllByChangeDayBetween(any(LocalDate.class), any(LocalDate.class)))
                .willReturn(List.of());

        // When
        List<AfterSchoolByTeacherResponseDto> result = afterSchoolService.getAfterSchoolsByTeacherId(teacherId);

        // Then
        assertThat(result).hasSize(1);
        
        AfterSchoolByTeacherResponseDto dto = result.get(0);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.weekDay()).isEqualTo("월");
        assertThat(dto.period()).isEqualTo("7교시");
        assertThat(dto.name()).isEqualTo("수학 방과후");
        assertThat(dto.place().name()).isEqualTo("수학실");
        assertThat(dto.reinforcementCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("해당 선생님의 방과후가 없으면 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoAfterSchools() {
        // Given
        Long teacherId = 999L;
        given(afterSchoolRepository.findByTeacherIdWithRelations(teacherId))
                .willReturn(List.of());

        // When
        List<AfterSchoolByTeacherResponseDto> result = afterSchoolService.getAfterSchoolsByTeacherId(teacherId);

        // Then
        assertThat(result).isEmpty();
    }
}