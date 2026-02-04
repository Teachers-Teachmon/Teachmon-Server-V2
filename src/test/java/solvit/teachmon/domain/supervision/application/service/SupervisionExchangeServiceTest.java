package solvit.teachmon.domain.supervision.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionExchangeType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionExchangeRepository;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.supervision.exception.SupervisionExchangeNotFoundException;
import solvit.teachmon.domain.supervision.exception.SupervisionScheduleNotFoundException;
import solvit.teachmon.domain.supervision.exception.UnauthorizedSupervisionAccessException;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeAcceptRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRejectRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionExchangeResponseDto;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("감독 교체 서비스 테스트")
class SupervisionExchangeServiceTest {

    @Mock
    private SupervisionExchangeRepository supervisionExchangeRepository;

    @Mock
    private SupervisionScheduleRepository supervisionScheduleRepository;

    @Mock
    private TeacherRepository teacherRepository;

    private SupervisionExchangeService supervisionExchangeService;

    private TeacherEntity senderTeacher;
    private TeacherEntity recipientTeacher;
    private SupervisionScheduleEntity senderSchedule;
    private SupervisionScheduleEntity recipientSchedule;
    private SupervisionExchangeEntity exchangeEntity;

    @BeforeEach
    void setUp() {
        supervisionExchangeService = new SupervisionExchangeService(
                supervisionExchangeRepository,
                supervisionScheduleRepository,
                teacherRepository
        );

        senderTeacher = mock(TeacherEntity.class);
        recipientTeacher = mock(TeacherEntity.class);
        senderSchedule = mock(SupervisionScheduleEntity.class);
        recipientSchedule = mock(SupervisionScheduleEntity.class);
        exchangeEntity = mock(SupervisionExchangeEntity.class);

        given(senderTeacher.getId()).willReturn(1L);
        given(senderTeacher.getName()).willReturn("송혜정");
        given(recipientTeacher.getId()).willReturn(2L);
        given(recipientTeacher.getName()).willReturn("김선생");
        
        given(senderSchedule.getTeacher()).willReturn(senderTeacher);
        given(senderSchedule.getDay()).willReturn(LocalDate.of(2025, 3, 2));
        given(senderSchedule.getType()).willReturn(SupervisionType.SELF_STUDY_SUPERVISION);
        
        given(recipientSchedule.getTeacher()).willReturn(recipientTeacher);
        given(recipientSchedule.getDay()).willReturn(LocalDate.of(2025, 3, 3));
        given(recipientSchedule.getType()).willReturn(SupervisionType.LEAVE_SEAT_SUPERVISION);
        
        given(exchangeEntity.getSender()).willReturn(senderTeacher);
        given(exchangeEntity.getRecipient()).willReturn(recipientTeacher);
        given(exchangeEntity.getSenderSchedule()).willReturn(senderSchedule);
        given(exchangeEntity.getRecipientSchedule()).willReturn(recipientSchedule);
        given(exchangeEntity.getReason()).willReturn("개인 사유");
        given(exchangeEntity.getState()).willReturn(SupervisionExchangeType.PENDING);
    }

    @Test
    @DisplayName("감독 교체 요청이 성공적으로 생성된다")
    void shouldCreateSupervisionExchangeRequestSuccessfully() {
        // Given
        SupervisionExchangeRequestDto requestDto = SupervisionExchangeRequestDto.builder()
                .requestorSupervisionId(1L)
                .changeSupervisionId(2L)
                .reason("개인 사유로 교체 요청드립니다")
                .build();
        Long requesterId = 1L;

        given(supervisionScheduleRepository.findById(1L)).willReturn(Optional.of(senderSchedule));
        given(supervisionScheduleRepository.findById(2L)).willReturn(Optional.of(recipientSchedule));
        given(teacherRepository.findById(1L)).willReturn(Optional.of(senderTeacher));
        given(supervisionExchangeRepository.save(any(SupervisionExchangeEntity.class))).willReturn(exchangeEntity);

        // When
        supervisionExchangeService.createSupervisionExchangeRequest(requestDto, requesterId);

        // Then
        verify(supervisionScheduleRepository).findById(1L);
        verify(supervisionScheduleRepository).findById(2L);
        verify(teacherRepository).findById(1L);
        verify(supervisionExchangeRepository).save(any(SupervisionExchangeEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 감독 일정으로 교체 요청 시 예외가 발생한다")
    void shouldThrowExceptionWhenSupervisionScheduleNotExists() {
        // Given
        SupervisionExchangeRequestDto requestDto = SupervisionExchangeRequestDto.builder()
                .requestorSupervisionId(999L)
                .changeSupervisionId(2L)
                .reason("개인 사유")
                .build();
        Long requesterId = 1L;

        given(supervisionScheduleRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeService.createSupervisionExchangeRequest(requestDto, requesterId))
                .isInstanceOf(SupervisionScheduleNotFoundException.class)
                .hasMessage("요청한 감독 일정을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 교사로 교체 요청 시 예외가 발생한다")
    void shouldThrowExceptionWhenTeacherNotExists() {
        // Given
        SupervisionExchangeRequestDto requestDto = SupervisionExchangeRequestDto.builder()
                .requestorSupervisionId(1L)
                .changeSupervisionId(2L)
                .reason("개인 사유")
                .build();
        Long requesterId = 999L;

        given(supervisionScheduleRepository.findById(1L)).willReturn(Optional.of(senderSchedule));
        given(supervisionScheduleRepository.findById(2L)).willReturn(Optional.of(recipientSchedule));
        given(teacherRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeService.createSupervisionExchangeRequest(requestDto, requesterId))
                .isInstanceOf(TeacherNotFoundException.class)
                .hasMessage("요청한 교사를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("교체 요청을 성공적으로 수락한다")
    void shouldAcceptSupervisionExchangeSuccessfully() {
        // Given
        SupervisionExchangeAcceptRequestDto requestDto = SupervisionExchangeAcceptRequestDto.builder()
                .exchangeRequestId(1L)
                .build();

        given(supervisionExchangeRepository.findById(1L)).willReturn(Optional.of(exchangeEntity));

        // When
        supervisionExchangeService.acceptSupervisionExchange(requestDto, 2L);

        // Then
        verify(supervisionExchangeRepository).findById(1L);
        verify(exchangeEntity).accept();
    }

    @Test
    @DisplayName("존재하지 않는 교체 요청을 수락 시 예외가 발생한다")
    void shouldThrowExceptionWhenExchangeRequestNotExistsForAccept() {
        // Given
        SupervisionExchangeAcceptRequestDto requestDto = SupervisionExchangeAcceptRequestDto.builder()
                .exchangeRequestId(999L)
                .build();

        given(supervisionExchangeRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeService.acceptSupervisionExchange(requestDto, 2L))
                .isInstanceOf(SupervisionExchangeNotFoundException.class)
                .hasMessage("요청한 감독 교체 요청을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("교체 요청을 성공적으로 거절한다")
    void shouldRejectSupervisionExchangeSuccessfully() {
        // Given
        SupervisionExchangeRejectRequestDto requestDto = SupervisionExchangeRejectRequestDto.builder()
                .exchangeRequestId(1L)
                .build();

        given(supervisionExchangeRepository.findById(1L)).willReturn(Optional.of(exchangeEntity));

        // When
        supervisionExchangeService.rejectSupervisionExchange(requestDto, 2L);

        // Then
        verify(supervisionExchangeRepository).findById(1L);
        verify(exchangeEntity).reject();
    }

    @Test
    @DisplayName("존재하지 않는 교체 요청을 거절 시 예외가 발생한다")
    void shouldThrowExceptionWhenExchangeRequestNotExistsForReject() {
        // Given
        SupervisionExchangeRejectRequestDto requestDto = SupervisionExchangeRejectRequestDto.builder()
                .exchangeRequestId(999L)
                .build();

        given(supervisionExchangeRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeService.rejectSupervisionExchange(requestDto, 2L))
                .isInstanceOf(SupervisionExchangeNotFoundException.class)
                .hasMessage("요청한 감독 교체 요청을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("모든 교체 요청 목록을 성공적으로 조회한다")
    void shouldGetSupervisionExchangesSuccessfully() {
        // Given
        List<SupervisionExchangeEntity> exchanges = List.of(exchangeEntity);
        given(supervisionExchangeRepository.findAll()).willReturn(exchanges);

        // When
        List<SupervisionExchangeResponseDto> result = supervisionExchangeService.getSupervisionExchanges();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).reason()).isEqualTo("개인 사유");
        assertThat(result.get(0).status()).isEqualTo(SupervisionExchangeType.PENDING);
        assertThat(result.get(0).requestor().teacher().name()).isEqualTo("송혜정");
        assertThat(result.get(0).responser().teacher().name()).isEqualTo("김선생");
        assertThat(result.get(0).requestor().type()).isEqualTo("self_study");
        assertThat(result.get(0).responser().type()).isEqualTo("leave_seat");

        verify(supervisionExchangeRepository).findAll();
    }

    @Test
    @DisplayName("빈 교체 요청 목록을 조회한다")
    void shouldReturnEmptyListWhenNoExchangeExists() {
        // Given
        given(supervisionExchangeRepository.findAll()).willReturn(List.of());

        // When
        List<SupervisionExchangeResponseDto> result = supervisionExchangeService.getSupervisionExchanges();

        // Then
        assertThat(result).isEmpty();
        verify(supervisionExchangeRepository).findAll();
    }

    @Test
    @DisplayName("본인의 감독 일정이 아닌 경우 교체 요청 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenUnauthorizedUser() {
        // Given
        SupervisionExchangeRequestDto requestDto = SupervisionExchangeRequestDto.builder()
                .requestorSupervisionId(1L)
                .changeSupervisionId(2L)
                .reason("개인 사유")
                .build();
        Long unauthorizedUserId = 999L; // senderSchedule의 teacher ID(1L)와 다름

        given(supervisionScheduleRepository.findById(1L)).willReturn(Optional.of(senderSchedule));
        given(supervisionScheduleRepository.findById(2L)).willReturn(Optional.of(recipientSchedule));

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeService.createSupervisionExchangeRequest(requestDto, unauthorizedUserId))
                .isInstanceOf(UnauthorizedSupervisionAccessException.class)
                .hasMessage("본인의 감독 일정에 대해서만 교체 요청을 할 수 있습니다.");
    }

    @Test
    @DisplayName("수신자가 아닌 사용자가 교체 요청 수락 시 예외가 발생한다")
    void shouldThrowExceptionWhenUnauthorizedUserAccept() {
        // Given
        SupervisionExchangeAcceptRequestDto requestDto = SupervisionExchangeAcceptRequestDto.builder()
                .exchangeRequestId(1L)
                .build();
        Long unauthorizedUserId = 999L; // exchangeEntity의 recipient ID(2L)와 다름

        given(supervisionExchangeRepository.findById(1L)).willReturn(Optional.of(exchangeEntity));

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeService.acceptSupervisionExchange(requestDto, unauthorizedUserId))
                .isInstanceOf(UnauthorizedSupervisionAccessException.class)
                .hasMessage("해당 교체 요청의 수신자만 수락할 수 있습니다.");
    }

    @Test
    @DisplayName("수신자가 아닌 사용자가 교체 요청 거절 시 예외가 발생한다")
    void shouldThrowExceptionWhenUnauthorizedUserReject() {
        // Given
        SupervisionExchangeRejectRequestDto requestDto = SupervisionExchangeRejectRequestDto.builder()
                .exchangeRequestId(1L)
                .build();
        Long unauthorizedUserId = 999L; // exchangeEntity의 recipient ID(2L)와 다름

        given(supervisionExchangeRepository.findById(1L)).willReturn(Optional.of(exchangeEntity));

        // When & Then
        assertThatThrownBy(() -> supervisionExchangeService.rejectSupervisionExchange(requestDto, unauthorizedUserId))
                .isInstanceOf(UnauthorizedSupervisionAccessException.class)
                .hasMessage("해당 교체 요청의 수신자만 거절할 수 있습니다.");
    }
}