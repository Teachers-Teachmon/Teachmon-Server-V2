package solvit.teachmon.domain.supervision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.supervision.domain.dto.DailySupervisionAssignment;
import solvit.teachmon.domain.supervision.domain.dto.TeacherPriorityInfo;
import solvit.teachmon.domain.supervision.domain.dto.TeacherSupervisionInfo;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionAutoAssignRepository;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.supervision.domain.strategy.SupervisionPriorityStrategy;
import solvit.teachmon.domain.supervision.exception.InsufficientTeachersException;
import solvit.teachmon.domain.supervision.exception.InvalidAssignmentException;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 감독 일정 자동 배정 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupervisionAutoAssignService {

    private final SupervisionAutoAssignRepository autoAssignRepository;
    private final SupervisionScheduleRepository scheduleRepository;
    private final TeacherRepository teacherRepository;
    private final SupervisionPriorityStrategy priorityStrategy;


    /**
     * 지정된 기간동안 감독 일정을 자동 배정
     * 월~목 평일에만 배정하며, 우선순위 알고리즘 적용
     */
    @Transactional
    public List<SupervisionScheduleResponseDto> autoAssignSupervisionSchedules(LocalDate startDate, LocalDate endDate) {
        log.info("감독 자동 배정 시작: startDate={}, endDate={}", startDate, endDate);

        List<TeacherSupervisionInfo> teacherInfos = getTeacherSupervisionInfos();
        List<LocalDate> targetDates = extractWeekdays(startDate, endDate);
        
        logInitialInfo(teacherInfos, targetDates);
        
        List<SupervisionScheduleEntity> schedules = processDateAssignments(targetDates, teacherInfos);
        List<SupervisionScheduleEntity> savedSchedules = scheduleRepository.saveAll(schedules);
        
        log.info("감독 자동 배정 완료: 총 {}개 스케줄 생성", savedSchedules.size());
        return convertToResponseDtos(savedSchedules);
    }

    private void logInitialInfo(List<TeacherSupervisionInfo> teacherInfos, List<LocalDate> targetDates) {
        log.info("배정 가능한 교사 수: {}", teacherInfos.size());
        log.info("배정 대상 날짜 수: {}", targetDates.size());
    }

    private List<SupervisionScheduleEntity> processDateAssignments(List<LocalDate> targetDates, 
                                                                  List<TeacherSupervisionInfo> teacherInfos) {
        List<SupervisionScheduleEntity> schedules = new ArrayList<>();
        Map<Long, TeacherSupervisionInfo> teacherInfoMap = createTeacherInfoMap(teacherInfos);

        for (LocalDate date : targetDates) {
            if (isScheduleAlreadyExists(date)) {
                log.debug("이미 스케줄 존재하여 스킵: {}", date);
                continue;
            }

            processSingleDateAssignment(date, teacherInfoMap, schedules);
        }
        
        return schedules;
    }

    private void processSingleDateAssignment(LocalDate date, 
                                           Map<Long, TeacherSupervisionInfo> teacherInfoMap,
                                           List<SupervisionScheduleEntity> schedules) {
        try {
            DailySupervisionAssignment assignment = assignDailySupervision(teacherInfoMap.values(), date);
            
            List<SupervisionScheduleEntity> dailySchedules = assignment.toScheduleEntities(date, teacherRepository);
            schedules.addAll(dailySchedules);

            updateTeacherInfosAfterAssignment(teacherInfoMap, assignment, date);
            logDailyAssignmentSuccess(date, assignment);

        } catch (InsufficientTeachersException | InvalidAssignmentException | TeacherNotFoundException e) {
            log.warn("날짜 {} 배정 실패: {}", date, e.getMessage());
        }
    }

    private void logDailyAssignmentSuccess(LocalDate date, DailySupervisionAssignment assignment) {
        log.debug("날짜 {} 배정 완료: 자습감독={}, 이석감독={}", 
                date, 
                assignment.selfStudyTeacher().teacherName(), 
                assignment.leaveSeatTeacher().teacherName());
    }

    /**
     * 특정 날짜의 감독 배정 (핵심 우선순위 알고리즘)
     */
    private DailySupervisionAssignment assignDailySupervision(Collection<TeacherSupervisionInfo> teacherInfos, LocalDate date) {
        List<TeacherSupervisionInfo> availableTeachers = getAvailableTeachers(teacherInfos, date);
        List<TeacherPriorityInfo> prioritizedTeachers = getPrioritizedTeachers(availableTeachers, date);
        
        validateSufficientTeachers(prioritizedTeachers, date);
        
        return createDailyAssignment(prioritizedTeachers, date);
    }

    private List<TeacherSupervisionInfo> getAvailableTeachers(Collection<TeacherSupervisionInfo> teacherInfos, LocalDate date) {
        List<TeacherSupervisionInfo> availableTeachers = teacherInfos.stream()
                .filter(info -> !info.isBanDay(date.getDayOfWeek()))
                .toList();

        if (availableTeachers.size() < 2) {
            throw new InsufficientTeachersException(
                    "배정 가능한 교사가 부족합니다. 날짜: " + date + ", 가능한 교사 수: " + availableTeachers.size());
        }
        
        return availableTeachers;
    }

    private List<TeacherPriorityInfo> getPrioritizedTeachers(List<TeacherSupervisionInfo> availableTeachers, LocalDate date) {
        return availableTeachers.stream()
                .map(info -> new TeacherPriorityInfo(info, priorityStrategy.calculatePriority(info, date)))
                .filter(priorityInfo -> priorityInfo.priority() > 0)
                .sorted(Comparator.comparing(TeacherPriorityInfo::priority).reversed())
                .toList();
    }

    private void validateSufficientTeachers(List<TeacherPriorityInfo> prioritizedTeachers, LocalDate date) {
        if (prioritizedTeachers.isEmpty()) {
            throw new InsufficientTeachersException(
                    "우선순위 계산 결과 배정 가능한 교사가 부족합니다. 날짜: " + date);
        }
    }

    private DailySupervisionAssignment createDailyAssignment(List<TeacherPriorityInfo> prioritizedTeachers, LocalDate date) {
        TeacherSupervisionInfo selfStudyTeacher = prioritizedTeachers.get(0).teacherInfo();
        TeacherSupervisionInfo leaveSeatTeacher = prioritizedTeachers.get(1).teacherInfo();

        validateDifferentTeachers(selfStudyTeacher, leaveSeatTeacher);
        logAssignmentResult(date, prioritizedTeachers, selfStudyTeacher, leaveSeatTeacher);

        return DailySupervisionAssignment.builder()
                .selfStudyTeacher(selfStudyTeacher)
                .leaveSeatTeacher(leaveSeatTeacher)
                .build();
    }

    private void validateDifferentTeachers(TeacherSupervisionInfo selfStudyTeacher, TeacherSupervisionInfo leaveSeatTeacher) {
        if (selfStudyTeacher.teacherId().equals(leaveSeatTeacher.teacherId())) {
            throw new InvalidAssignmentException("동일한 교사를 두 감독 타입에 배정할 수 없습니다.");
        }
    }

    private void logAssignmentResult(LocalDate date, List<TeacherPriorityInfo> prioritizedTeachers, 
                                   TeacherSupervisionInfo selfStudyTeacher, TeacherSupervisionInfo leaveSeatTeacher) {
        log.debug("날짜 {} 우선순위: 자습감독={}({}), 이석감독={}({})", 
                date, 
                selfStudyTeacher.teacherName(), prioritizedTeachers.get(0).priority(),
                leaveSeatTeacher.teacherName(), prioritizedTeachers.get(1).priority());
    }

    /**
     * 교사 감독 정보 조회 및 조합
     */
    private List<TeacherSupervisionInfo> getTeacherSupervisionInfos() {
        List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> teacherProjections = 
                getTeacherProjections();
        
        List<Long> teacherIds = extractTeacherIds(teacherProjections);
        Map<Long, Set<WeekDay>> banDaysByTeacher = getBanDaysByTeacher(teacherIds);
        
        return buildTeacherSupervisionInfos(teacherProjections, banDaysByTeacher);
    }

    private List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> getTeacherProjections() {
        List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> teacherProjections =
                autoAssignRepository.findTeacherSupervisionInfoByRole(Role.TEACHER);

        if (teacherProjections.isEmpty()) {
            throw new InsufficientTeachersException("감독 배정 가능한 교사가 없습니다.");
        }
        
        return teacherProjections;
    }

    private List<Long> extractTeacherIds(List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> projections) {
        return projections.stream()
                .map(SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection::getTeacherId)
                .toList();
    }

    private Map<Long, Set<WeekDay>> getBanDaysByTeacher(List<Long> teacherIds) {
        List<SupervisionAutoAssignRepository.SupervisionBanDayProjection> banDayProjections =
                autoAssignRepository.findBanDaysByTeacherIds(teacherIds);

        return banDayProjections.stream()
                .collect(Collectors.groupingBy(
                        SupervisionAutoAssignRepository.SupervisionBanDayProjection::getTeacherId,
                        Collectors.mapping(
                                SupervisionAutoAssignRepository.SupervisionBanDayProjection::getWeekDay,
                                Collectors.toSet()
                        )
                ));
    }

    private List<TeacherSupervisionInfo> buildTeacherSupervisionInfos(
            List<SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection> projections,
            Map<Long, Set<WeekDay>> banDaysByTeacher) {
        return projections.stream()
                .map(projection -> createTeacherSupervisionInfo(projection, banDaysByTeacher))
                .toList();
    }

    private TeacherSupervisionInfo createTeacherSupervisionInfo(
            SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection projection,
            Map<Long, Set<WeekDay>> banDaysByTeacher) {
        return TeacherSupervisionInfo.builder()
                .teacherId(projection.getTeacherId())
                .teacherName(projection.getTeacherName())
                .banDays(banDaysByTeacher.getOrDefault(projection.getTeacherId(), Set.of()))
                .lastSupervisionDate(projection.getLastSupervisionDate())
                .totalSupervisionCount(getSupervisionCount(projection))
                .supervisionCounts(new HashMap<>())
                .build();
    }

    private int getSupervisionCount(SupervisionAutoAssignRepository.TeacherSupervisionInfoProjection projection) {
        return projection.getTotalSupervisionCount() != null ? 
                projection.getTotalSupervisionCount().intValue() : 0;
    }

    /**
     * 평일 날짜만 추출 (월~목)
     */
    private List<LocalDate> extractWeekdays(LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate.plusDays(1))
                .filter(date -> {
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    return dayOfWeek == DayOfWeek.MONDAY ||
                            dayOfWeek == DayOfWeek.TUESDAY ||
                            dayOfWeek == DayOfWeek.WEDNESDAY ||
                            dayOfWeek == DayOfWeek.THURSDAY;
                })
                .toList();
    }


    /**
     * 기존 스케줄 존재 여부 확인
     */
    private boolean isScheduleAlreadyExists(LocalDate date) {
        return autoAssignRepository.existsScheduleByDate(date);
    }

    /**
     * 교사 정보를 Map으로 변환 (빠른 업데이트를 위해)
     */
    private Map<Long, TeacherSupervisionInfo> createTeacherInfoMap(List<TeacherSupervisionInfo> teacherInfos) {
        return teacherInfos.stream()
                .collect(Collectors.toMap(
                        TeacherSupervisionInfo::teacherId,
                        info -> info
                ));
    }

    /**
     * 감독 배정 후 교사 정보 업데이트 (메모리상에서)
     */
    private void updateTeacherInfosAfterAssignment(Map<Long, TeacherSupervisionInfo> teacherInfoMap,
                                                  DailySupervisionAssignment assignment, LocalDate date) {
        // 자습 감독 교사 정보 업데이트
        Long selfStudyTeacherId = assignment.selfStudyTeacher().teacherId();
        teacherInfoMap.put(selfStudyTeacherId,
                assignment.selfStudyTeacher().withUpdatedSupervision(date, SupervisionType.SELF_STUDY_SUPERVISION));

        // 이석 감독 교사 정보 업데이트
        Long leaveSeatTeacherId = assignment.leaveSeatTeacher().teacherId();
        teacherInfoMap.put(leaveSeatTeacherId,
                assignment.leaveSeatTeacher().withUpdatedSupervision(date, SupervisionType.LEAVE_SEAT_SUPERVISION));
    }

    /**
     * SupervisionScheduleEntity를 ResponseDto로 변환
     */
    private List<SupervisionScheduleResponseDto> convertToResponseDtos(List<SupervisionScheduleEntity> schedules) {
        Map<LocalDate, List<SupervisionScheduleEntity>> schedulesByDay = 
                schedules.stream().collect(Collectors.groupingBy(SupervisionScheduleEntity::getDay));
                
        return schedulesByDay.entrySet().stream()
                .map(this::createDayResponseDto)
                .sorted(Comparator.comparing(SupervisionScheduleResponseDto::day))
                .toList();
    }

    private SupervisionScheduleResponseDto createDayResponseDto(Map.Entry<LocalDate, List<SupervisionScheduleEntity>> entry) {
        LocalDate day = entry.getKey();
        List<SupervisionScheduleEntity> daySchedules = entry.getValue();

        return SupervisionScheduleResponseDto.builder()
                .day(day)
                .selfStudySupervision(findSupervisionByType(daySchedules, SupervisionType.SELF_STUDY_SUPERVISION))
                .leaveSeatSupervision(findSupervisionByType(daySchedules, SupervisionType.LEAVE_SEAT_SUPERVISION))
                .build();
    }

    /**
     * 감독 타입별로 SupervisionInfo 추출
     */
    private SupervisionScheduleResponseDto.SupervisionInfo findSupervisionByType(
            List<SupervisionScheduleEntity> schedules, SupervisionType type) {
        return schedules.stream()
                .filter(schedule -> schedule.getType() == type)
                .findFirst()
                .map(this::createSupervisionInfo)
                .orElse(null);
    }

    /**
     * SupervisionScheduleEntity를 SupervisionInfo로 변환
     */
    private SupervisionScheduleResponseDto.SupervisionInfo createSupervisionInfo(SupervisionScheduleEntity schedule) {
        return SupervisionScheduleResponseDto.SupervisionInfo.builder()
                .id(schedule.getId())
                .teacher(SupervisionScheduleResponseDto.SupervisionInfo.TeacherInfo.builder()
                        .id(schedule.getTeacher().getId())
                        .name(schedule.getTeacher().getName())
                        .build())
                .build();
    }
}