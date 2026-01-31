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
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolBusinessTripRequestDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 출장 컨트롤러 테스트")
class AfterSchoolControllerBusinessTripTest {

    @InjectMocks
    private AfterSchoolController afterSchoolController;

    @Mock
    private AfterSchoolService afterSchoolService;

    private AfterSchoolBusinessTripRequestDto businessTripRequest;

    @BeforeEach
    void setUp() {
        businessTripRequest = new AfterSchoolBusinessTripRequestDto(
                LocalDate.now().plusDays(10),
                107786687L
        );
    }

    @Test
    @DisplayName("유효한 요청으로 출장을 성공적으로 생성한다")
    void shouldCreateBusinessTripSuccessfully() {
        // Given
        willDoNothing().given(afterSchoolService).createBusinessTrip(any(AfterSchoolBusinessTripRequestDto.class));

        // When
        ResponseEntity<Void> response = afterSchoolController.createBusinessTrip(businessTripRequest);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(afterSchoolService).createBusinessTrip(any(AfterSchoolBusinessTripRequestDto.class));
    }

    @Test
    @DisplayName("존재하지 않는 방과후 ID로 요청 시 예외가 발생한다")
    void shouldThrowExceptionWhenAfterSchoolNotExists() {
        // Given
        AfterSchoolBusinessTripRequestDto invalidRequest = new AfterSchoolBusinessTripRequestDto(
                LocalDate.now().plusDays(10),
                999999L
        );
        willThrow(new AfterSchoolNotFoundException(999999L))
                .given(afterSchoolService).createBusinessTrip(any(AfterSchoolBusinessTripRequestDto.class));

        // When & Then
        assertThatThrownBy(() -> afterSchoolController.createBusinessTrip(invalidRequest))
                .isInstanceOf(AfterSchoolNotFoundException.class);
    }
}