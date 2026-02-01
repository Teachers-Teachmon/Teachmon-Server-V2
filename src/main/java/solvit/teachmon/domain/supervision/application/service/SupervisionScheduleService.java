package solvit.teachmon.domain.supervision.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionScheduleCreateRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionScheduleDeleteRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionTodayResponseDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionRankResponseDto;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionTodayType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionSortOrder;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupervisionScheduleService {

    private final SupervisionScheduleRepository supervisionScheduleRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    public void createSupervisionSchedule(SupervisionScheduleCreateRequestDto requestDto) {
        createSupervisionSchedulesInternal(requestDto);
    }

    @Transactional
    public void updateSupervisionSchedule(SupervisionScheduleCreateRequestDto requestDto) {
        // 해당 날짜의 기존 감독 일정들을 모두 삭제
        supervisionScheduleRepository.deleteByDay(requestDto.day());
        
        // 새로운 감독 일정 생성
        createSupervisionSchedulesInternal(requestDto);
    }

    private void createSupervisionSchedulesInternal(SupervisionScheduleCreateRequestDto requestDto) {
        // 교사 조회
        TeacherEntity selfStudyTeacher = teacherRepository.findById(requestDto.selfStudySupervisionTeacherId())
                .orElseThrow(TeacherNotFoundException::new);
        
        TeacherEntity leaveSeatTeacher = teacherRepository.findById(requestDto.leaveSeatSupervisionTeacherId())
                .orElseThrow(TeacherNotFoundException::new);

        // 7~11교시까지 모든 교시에 대해 감독 일정 생성
        SchoolPeriod[] periods = {
            SchoolPeriod.SEVEN_PERIOD,
            SchoolPeriod.EIGHT_AND_NINE_PERIOD,
            SchoolPeriod.TEN_AND_ELEVEN_PERIOD
        };

        // 모든 감독 일정을 리스트에 담아서 일괄 저장
        List<SupervisionScheduleEntity> schedules = new ArrayList<>();
        
        for (SchoolPeriod period : periods) {
            // 자습 감독 일정 생성
            SupervisionScheduleEntity selfStudySchedule = SupervisionScheduleEntity.builder()
                    .teacher(selfStudyTeacher)
                    .day(requestDto.day())
                    .period(period)
                    .type(SupervisionType.SELF_STUDY_SUPERVISION)
                    .build();
            schedules.add(selfStudySchedule);

            // 이석 감독 일정 생성
            SupervisionScheduleEntity leaveSeatSchedule = SupervisionScheduleEntity.builder()
                    .teacher(leaveSeatTeacher)
                    .day(requestDto.day())
                    .period(period)
                    .type(SupervisionType.LEAVE_SEAT_SUPERVISION)
                    .build();
            schedules.add(leaveSeatSchedule);
        }
        
        supervisionScheduleRepository.saveAll(schedules);
    }

    @Transactional
    public void deleteSupervisionSchedule(SupervisionScheduleDeleteRequestDto requestDto) {
        if (requestDto.type().isAll()) {
            supervisionScheduleRepository.deleteByDay(requestDto.day());
        } else {
            supervisionScheduleRepository.deleteByDayAndType(requestDto.day(), requestDto.type().toSupervisionType());
        }
    }

    @Transactional(readOnly = true)
    public List<SupervisionScheduleResponseDto> searchSupervisionSchedules(Integer month, String query) {
        List<SupervisionScheduleEntity> schedules = supervisionScheduleRepository.findByMonthAndQuery(month, query);
        
        Map<LocalDate, List<SupervisionScheduleEntity>> groupedByDay = schedules.stream()
                .collect(Collectors.groupingBy(SupervisionScheduleEntity::getDay));
        
        return groupedByDay.entrySet().stream()
                .map(this::convertToResponseDto)
                .sorted(Comparator.comparing(SupervisionScheduleResponseDto::day))
                .collect(Collectors.toList());
    }

    private SupervisionScheduleResponseDto convertToResponseDto(Map.Entry<LocalDate, List<SupervisionScheduleEntity>> entry) {
        LocalDate day = entry.getKey();
        List<SupervisionScheduleEntity> daySchedules = entry.getValue();
        
        SupervisionScheduleResponseDto.SupervisionInfo selfStudySupervision = findSupervisionByType(daySchedules, SupervisionType.SELF_STUDY_SUPERVISION);
        SupervisionScheduleResponseDto.SupervisionInfo leaveSeatSupervision = findSupervisionByType(daySchedules, SupervisionType.LEAVE_SEAT_SUPERVISION);
        
        return SupervisionScheduleResponseDto.builder()
                .day(day)
                .selfStudySupervision(selfStudySupervision)
                .leaveSeatSupervision(leaveSeatSupervision)
                .build();
    }

    private SupervisionScheduleResponseDto.SupervisionInfo findSupervisionByType(
            List<SupervisionScheduleEntity> schedules, SupervisionType type) {
        return schedules.stream()
                .filter(schedule -> schedule.getType() == type)
                .findFirst()
                .map(this::convertToSupervisionInfo)
                .orElse(null);
    }

    private SupervisionScheduleResponseDto.SupervisionInfo convertToSupervisionInfo(SupervisionScheduleEntity schedule) {
        return SupervisionScheduleResponseDto.SupervisionInfo.builder()
                .id(schedule.getId())
                .teacher(convertToTeacherInfo(schedule.getTeacher()))
                .build();
    }

    private SupervisionScheduleResponseDto.SupervisionInfo.TeacherInfo convertToTeacherInfo(TeacherEntity teacher) {
        return SupervisionScheduleResponseDto.SupervisionInfo.TeacherInfo.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public List<LocalDate> getMySupervisionDays(Long teacherId, Integer month) {
        return supervisionScheduleRepository.findSupervisionDaysByTeacherAndMonth(teacherId, month);
    }

    @Transactional(readOnly = true)
    public SupervisionTodayResponseDto getMyTodaySupervisionType(Long teacherId) {
        LocalDate today = LocalDate.now();
        List<SupervisionType> todayTypes = supervisionScheduleRepository.findTodaySupervisionTypesByTeacher(teacherId, today);
        
        boolean hasSelfStudy = todayTypes.contains(SupervisionType.SELF_STUDY_SUPERVISION);
        boolean hasLeaveSeat = todayTypes.contains(SupervisionType.LEAVE_SEAT_SUPERVISION);
        
        SupervisionTodayType todayType = SupervisionTodayType.from(hasSelfStudy, hasLeaveSeat);
        
        return SupervisionTodayResponseDto.builder()
                .type(todayType)
                .build();
    }

    @Transactional(readOnly = true)
    public List<SupervisionRankResponseDto> getSupervisionRankings(String query, String order) {
        // String을 enum으로 변환
        SupervisionSortOrder sortOrder = "desc".equals(order) ? SupervisionSortOrder.DESC : SupervisionSortOrder.ASC;
        
        // query가 null이거나 빈값이면 null로 통일
        String searchQuery = StringUtils.hasText(query) ? query.trim() : null;
        
        return supervisionScheduleRepository.findSupervisionRankings(searchQuery, sortOrder);
    }
}