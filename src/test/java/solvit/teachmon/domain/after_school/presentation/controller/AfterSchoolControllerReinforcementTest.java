package solvit.teachmon.domain.after_school.presentation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import solvit.teachmon.domain.after_school.application.service.AfterSchoolService;
import solvit.teachmon.domain.after_school.exception.AfterSchoolNotFoundException;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolReinforcementException;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolReinforcementRequestDto;
import solvit.teachmon.domain.place.exception.PlaceNotFoundException;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 보강 컨트롤러 테스트")
class AfterSchoolControllerReinforcementTest {

    @InjectMocks
    private AfterSchoolController afterSchoolController;

    @Mock
    private AfterSchoolService afterSchoolService;

    private AfterSchoolReinforcementRequestDto reinforcementRequest;

    @BeforeEach
    void setUp() {
        reinforcementRequest = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10),
                107786687L,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                536346L
        );
    }

    @Test
    @DisplayName("유효한 요청으로 보강을 성공적으로 생성한다")
    void shouldCreateReinforcementSuccessfully() {
        // Given
        willDoNothing().given(afterSchoolService).createReinforcement(any(AfterSchoolReinforcementRequestDto.class));

        // When
        ResponseEntity<Void> response = afterSchoolController.createReinforcement(reinforcementRequest);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(afterSchoolService).createReinforcement(any(AfterSchoolReinforcementRequestDto.class));
    }

    @Test
    @DisplayName("존재하지 않는 방과후 ID로 요청 시 예외가 발생한다")
    void shouldThrowExceptionWhenAfterSchoolNotExists() {
        // Given
        AfterSchoolReinforcementRequestDto invalidRequest = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10),
                999999L,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                536346L
        );
        willThrow(new AfterSchoolNotFoundException(999999L))
                .given(afterSchoolService).createReinforcement(any(AfterSchoolReinforcementRequestDto.class));

        // When & Then
        assertThatThrownBy(() -> afterSchoolController.createReinforcement(invalidRequest))
                .isInstanceOf(AfterSchoolNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 장소 ID로 요청 시 예외가 발생한다")
    void shouldThrowExceptionWhenPlaceNotExists() {
        // Given
        AfterSchoolReinforcementRequestDto invalidRequest = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10),
                107786687L,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                999999L
        );
        willThrow(new PlaceNotFoundException())
                .given(afterSchoolService).createReinforcement(any(AfterSchoolReinforcementRequestDto.class));

        // When & Then
        assertThatThrownBy(() -> afterSchoolController.createReinforcement(invalidRequest))
                .isInstanceOf(PlaceNotFoundException.class);
    }

    @Test
    @DisplayName("보강 교시가 누락되면 예외가 발생한다")
    void shouldThrowExceptionWhenChangePeriodIsNull() {
        // Given
        AfterSchoolReinforcementRequestDto invalidRequest = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10),
                107786687L,
                null,
                536346L
        );
        willThrow(new InvalidAfterSchoolReinforcementException("보강 교시는 필수입니다."))
                .given(afterSchoolService).createReinforcement(any(AfterSchoolReinforcementRequestDto.class));

        // When & Then
        assertThatThrownBy(() -> afterSchoolController.createReinforcement(invalidRequest))
                .isInstanceOf(InvalidAfterSchoolReinforcementException.class)
                .hasMessageContaining("보강 교시는 필수입니다.");
    }
}
