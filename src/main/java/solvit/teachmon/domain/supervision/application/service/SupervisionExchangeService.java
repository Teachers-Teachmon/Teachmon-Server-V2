package solvit.teachmon.domain.supervision.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionExchangeRepository;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.supervision.exception.SupervisionExchangeNotFoundException;
import solvit.teachmon.domain.supervision.exception.SupervisionScheduleNotFoundException;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeAcceptRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRejectRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionExchangeRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionExchangeResponseDto;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupervisionExchangeService {

    private final SupervisionExchangeRepository supervisionExchangeRepository;
    private final SupervisionScheduleRepository supervisionScheduleRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public void createSupervisionExchangeRequest(SupervisionExchangeRequestDto requestDto, Long requesterId) {
        // 감독 일정 조회
        SupervisionScheduleEntity requestorSchedule = supervisionScheduleRepository.findById(requestDto.requestorSupervisionId())
                .orElseThrow(SupervisionScheduleNotFoundException::new);
        
        SupervisionScheduleEntity changeSchedule = supervisionScheduleRepository.findById(requestDto.changeSupervisionId())
                .orElseThrow(SupervisionScheduleNotFoundException::new);

        // 교사 조회
        TeacherEntity requester = teacherRepository.findById(requesterId)
                .orElseThrow(TeacherNotFoundException::new);
        
        TeacherEntity recipient = changeSchedule.getTeacher();

        // 감독 교체 요청 생성
        SupervisionExchangeEntity exchangeEntity = SupervisionExchangeEntity.builder()
                .sender(requester)
                .recipient(recipient)
                .senderSchedule(requestorSchedule)
                .recipientSchedule(changeSchedule)
                .reason(requestDto.reason())
                .build();

        supervisionExchangeRepository.save(exchangeEntity);
    }

    @Transactional
    public void acceptSupervisionExchange(SupervisionExchangeAcceptRequestDto requestDto) {
        // 교체 요청 조회
        SupervisionExchangeEntity exchangeEntity = supervisionExchangeRepository.findById(requestDto.exchangeRequestId())
                .orElseThrow(SupervisionExchangeNotFoundException::new);

        // 교체 요청 수락
        exchangeEntity.accept();
    }

    @Transactional
    public void rejectSupervisionExchange(SupervisionExchangeRejectRequestDto requestDto) {
        // 교체 요청 조회
        SupervisionExchangeEntity exchangeEntity = supervisionExchangeRepository.findById(requestDto.exchangeRequestId())
                .orElseThrow(SupervisionExchangeNotFoundException::new);

        // 교체 요청 거절
        exchangeEntity.reject();
    }

    @Transactional(readOnly = true)
    public List<SupervisionExchangeResponseDto> getSupervisionExchanges() {
        List<SupervisionExchangeEntity> exchanges = supervisionExchangeRepository.findAll();
        
        return exchanges.stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    private SupervisionExchangeResponseDto convertToResponseDto(SupervisionExchangeEntity exchange) {
        return SupervisionExchangeResponseDto.builder()
                .id(exchange.getId())
                .requestor(convertToSupervisionInfo(exchange.getSenderSchedule()))
                .responser(convertToSupervisionInfo(exchange.getRecipientSchedule()))
                .status(exchange.getState())
                .reason(exchange.getReason())
                .build();
    }

    private SupervisionExchangeResponseDto.SupervisionInfo convertToSupervisionInfo(SupervisionScheduleEntity schedule) {
        return SupervisionExchangeResponseDto.SupervisionInfo.builder()
                .teacher(convertToTeacherInfo(schedule.getTeacher()))
                .day(schedule.getDay())
                .type(convertSupervisionType(schedule.getType()))
                .build();
    }

    private SupervisionExchangeResponseDto.SupervisionInfo.TeacherInfo convertToTeacherInfo(TeacherEntity teacher) {
        return SupervisionExchangeResponseDto.SupervisionInfo.TeacherInfo.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .build();
    }

    private String convertSupervisionType(SupervisionType type) {
        return switch (type) {
            case SELF_STUDY_SUPERVISION -> "self_study";
            case LEAVE_SEAT_SUPERVISION -> "leave_seat";
        };
    }
}