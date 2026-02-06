package solvit.teachmon.domain.supervision.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import solvit.teachmon.domain.supervision.application.service.SupervisionExchangeService;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionExchangeType;
import solvit.teachmon.domain.supervision.exception.SupervisionExchangeNotFoundException;
import solvit.teachmon.domain.supervision.exception.SupervisionScheduleNotFoundException;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeAcceptRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRejectRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionExchangeResponseDto;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.security.user.TeachmonUserDetails;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("감독 교체 컨트롤러 테스트")
class SupervisionExchangeControllerTest {

    @InjectMocks
    private SupervisionExchangeController supervisionExchangeController;

    @Mock
    private SupervisionExchangeService supervisionExchangeService;

    @Mock
    private TeachmonUserDetails userDetails;

    @Test
    @DisplayName("교체 요청 목록을 성공적으로 조회한다")
    void shouldGetSupervisionExchangesSuccessfully() {
        // Given
        SupervisionExchangeResponseDto.SupervisionInfo.TeacherInfo senderTeacher =
                SupervisionExchangeResponseDto.SupervisionInfo.TeacherInfo.builder()
                        .id(1L)
                        .name("송혜정")
                        .build();

        SupervisionExchangeResponseDto.SupervisionInfo.TeacherInfo recipientTeacher =
                SupervisionExchangeResponseDto.SupervisionInfo.TeacherInfo.builder()
                        .id(2L)
                        .name("김선생")
                        .build();

        SupervisionExchangeResponseDto.SupervisionInfo requestor =
                SupervisionExchangeResponseDto.SupervisionInfo.builder()
                        .teacher(senderTeacher)
                        .day(LocalDate.of(2025, 3, 2))
                        .type("self_study")
                        .build();

        SupervisionExchangeResponseDto.SupervisionInfo responser =
                SupervisionExchangeResponseDto.SupervisionInfo.builder()
                        .teacher(recipientTeacher)
                        .day(LocalDate.of(2025, 3, 3))
                        .type("leave_seat")
                        .build();

        SupervisionExchangeResponseDto response = SupervisionExchangeResponseDto.builder()
                .id(1L)
                .requestor(requestor)
                .responser(responser)
                .status(SupervisionExchangeType.PENDING)
                .reason("개인 사유로 교체 요청드립니다")
                .build();

        given(userDetails.getId()).willReturn(2L);
        given(supervisionExchangeService.getSupervisionExchanges(anyLong())).willReturn(List.of(response));

        // When
        ResponseEntity<List<SupervisionExchangeResponseDto>> result = supervisionExchangeController.getSupervisionExchanges(userDetails);

        // Then
        assertThat(result.getStatusCode().value()).isEqualTo(200);
        assertThat(result.getBody()).hasSize(1);
        assertThat(result.getBody().get(0).id()).isEqualTo(1L);
        assertThat(result.getBody().get(0).requestor().teacher().name()).isEqualTo("송혜정");
        assertThat(result.getBody().get(0).responser().teacher().name()).isEqualTo("김선생");
        assertThat(result.getBody().get(0).status()).isEqualTo(SupervisionExchangeType.PENDING);

        verify(supervisionExchangeService).getSupervisionExchanges(2L);
    }

    @Test
    @DisplayName("교체 요청을 성공적으로 생성한다")
    void shouldCreateSupervisionExchangeRequestSuccessfully() {
        // Given
        SupervisionExchangeRequestDto request = SupervisionExchangeRequestDto.builder()
                .requestorSupervisionId(1L)
                .changeSupervisionId(2L)
                .reason("개인 사유로 교체 요청드립니다")
                .build();

        given(userDetails.getId()).willReturn(1L);
        willDoNothing().given(supervisionExchangeService).createSupervisionExchangeRequest(any(), any());

        // When
        ResponseEntity<Void> result = supervisionExchangeController.createSupervisionExchangeRequest(request, userDetails);

        // Then
        assertThat(result.getStatusCode().value()).isEqualTo(204);

        verify(supervisionExchangeService).createSupervisionExchangeRequest(request, 1L);
    }

    @Test
    @DisplayName("존재하지 않는 감독 일정으로 요청 시 예외가 발생한다")
    void shouldThrowExceptionWhenSupervisionScheduleNotExists() {
        // Given
        SupervisionExchangeRequestDto request = SupervisionExchangeRequestDto.builder()
                .requestorSupervisionId(999L)
                .changeSupervisionId(2L)
                .reason("개인 사유")
                .build();

        given(userDetails.getId()).willReturn(1L);
        willThrow(new SupervisionScheduleNotFoundException())
                .given(supervisionExchangeService).createSupervisionExchangeRequest(any(), any());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeController.createSupervisionExchangeRequest(request, userDetails))
                .isInstanceOf(SupervisionScheduleNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 교사로 요청 시 예외가 발생한다")
    void shouldThrowExceptionWhenTeacherNotExists() {
        // Given
        SupervisionExchangeRequestDto request = SupervisionExchangeRequestDto.builder()
                .requestorSupervisionId(1L)
                .changeSupervisionId(2L)
                .reason("개인 사유")
                .build();

        given(userDetails.getId()).willReturn(999L);
        willThrow(new TeacherNotFoundException())
                .given(supervisionExchangeService).createSupervisionExchangeRequest(any(), any());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeController.createSupervisionExchangeRequest(request, userDetails))
                .isInstanceOf(TeacherNotFoundException.class);
    }

    @Test
    @DisplayName("교체 요청을 성공적으로 수락한다")
    void shouldAcceptSupervisionExchangeSuccessfully() {
        // Given
        SupervisionExchangeAcceptRequestDto request = SupervisionExchangeAcceptRequestDto.builder()
                .exchangeRequestId(1L)
                .build();

        given(userDetails.getId()).willReturn(1L);
        willDoNothing().given(supervisionExchangeService).acceptSupervisionExchange(any(), anyLong());

        // When
        ResponseEntity<Void> result = supervisionExchangeController.acceptSupervisionExchange(request, userDetails);

        // Then
        assertThat(result.getStatusCode().value()).isEqualTo(204);

        verify(supervisionExchangeService).acceptSupervisionExchange(request, 1L);
    }

    @Test
    @DisplayName("존재하지 않는 교체 요청을 수락 시 예외가 발생한다")
    void shouldThrowExceptionWhenExchangeRequestNotExistsForAccept() {
        // Given
        SupervisionExchangeAcceptRequestDto request = SupervisionExchangeAcceptRequestDto.builder()
                .exchangeRequestId(999L)
                .build();

        given(userDetails.getId()).willReturn(1L);
        willThrow(new SupervisionExchangeNotFoundException())
                .given(supervisionExchangeService).acceptSupervisionExchange(any(), anyLong());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeController.acceptSupervisionExchange(request, userDetails))
                .isInstanceOf(SupervisionExchangeNotFoundException.class);
    }

    @Test
    @DisplayName("교체 요청을 성공적으로 거절한다")
    void shouldRejectSupervisionExchangeSuccessfully() {
        // Given
        SupervisionExchangeRejectRequestDto request = SupervisionExchangeRejectRequestDto.builder()
                .exchangeRequestId(1L)
                .build();

        given(userDetails.getId()).willReturn(1L);
        willDoNothing().given(supervisionExchangeService).rejectSupervisionExchange(any(), anyLong());

        // When
        ResponseEntity<Void> result = supervisionExchangeController.rejectSupervisionExchange(request, userDetails);

        // Then
        assertThat(result.getStatusCode().value()).isEqualTo(204);

        verify(supervisionExchangeService).rejectSupervisionExchange(request, 1L);
    }

    @Test
    @DisplayName("존재하지 않는 교체 요청을 거절 시 예외가 발생한다")
    void shouldThrowExceptionWhenExchangeRequestNotExistsForReject() {
        // Given
        SupervisionExchangeRejectRequestDto request = SupervisionExchangeRejectRequestDto.builder()
                .exchangeRequestId(999L)
                .build();

        given(userDetails.getId()).willReturn(1L);
        willThrow(new SupervisionExchangeNotFoundException())
                .given(supervisionExchangeService).rejectSupervisionExchange(any(), anyLong());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeController.rejectSupervisionExchange(request, userDetails))
                .isInstanceOf(SupervisionExchangeNotFoundException.class);
    }
}