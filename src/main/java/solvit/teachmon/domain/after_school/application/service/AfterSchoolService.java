package solvit.teachmon.domain.after_school.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolStudentEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.service.AfterSchoolStudentDomainService;
import solvit.teachmon.domain.after_school.domain.vo.StudentAssignmentResultVo;
import solvit.teachmon.domain.after_school.exception.AfterSchoolBusinessTripScheduleNotFoundException;
import solvit.teachmon.domain.after_school.exception.AfterSchoolNotFoundException;
import solvit.teachmon.domain.after_school.exception.PlaceAlreadyBookedException;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolBusinessTripRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolCreateRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolReinforcementRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolSearchRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolUpdateRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.*;
import solvit.teachmon.domain.after_school.presentation.dto.response.StudentInfo;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.exception.InvalidStudentInfoException;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDayEntity;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.place.exception.PlaceNotFoundException;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AfterSchoolScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AfterSchoolService {
    private final AfterSchoolStudentDomainService afterSchoolStudentDomainService;
    private final SupervisionBanDayRepository supervisionBanDayRepository;
    private final AfterSchoolRepository afterSchoolRepository;
    private final AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;
    private final AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final BranchRepository branchRepository;
    private final PlaceRepository placeRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final AfterSchoolScheduleService afterSchoolScheduleService;
    private final AfterSchoolScheduleRepository afterSchoolScheduleRepository;

    @Transactional
    public void createAfterSchool(AfterSchoolCreateRequestDto requestDto) {
        TeacherEntity teacher = getTeacherById(requestDto.teacherId());
        PlaceEntity place = getPlaceById(requestDto.placeId());
        BranchEntity branch = getBranchByYearAndId(requestDto.year(), requestDto.branch());
        List<StudentEntity> students = fetchStudentsByIds(requestDto.studentsId());

        validateStudentsGrade(students, requestDto.grade());

        AfterSchoolEntity afterSchool = AfterSchoolEntity.builder()
                .teacher(teacher)
                .branch(branch)
                .place(place)
                .weekDay(requestDto.weekDay())
                .period(requestDto.period())
                .name(requestDto.name())
                .grade(requestDto.grade())
                .year(requestDto.year())
                .build();

        afterSchoolRepository.save(afterSchool);

        StudentAssignmentResultVo studentAssignmentResultVo = afterSchoolStudentDomainService.assignStudents(afterSchool, students);
        afterSchoolScheduleService.save(List.of(studentAssignmentResultVo));

        SupervisionBanDayEntity supervisionBanDayEntity = SupervisionBanDayEntity.builder()
                .teacher(teacher)
                .weekDay(requestDto.weekDay())
                .isAfterschool(true)
                .build();

        supervisionBanDayRepository.save(supervisionBanDayEntity);
    }

    @Transactional
    public void updateAfterSchool(AfterSchoolUpdateRequestDto requestDto) {
        if (isMergedUpdateRequest(requestDto)) {
            updateMergedAfterSchool(requestDto);
            return;
        }

        updateSingleAfterSchool(requestDto);
    }

    @Transactional
    public void updateSingleAfterSchool(AfterSchoolUpdateRequestDto requestDto) {
        AfterSchoolEntity afterSchool = getAfterSchoolById(parseSingleAfterSchoolId(requestDto.afterSchoolId()));
        supervisionBanDayRepository.deleteAfterSchoolBanDay(afterSchool.getTeacher().getId(), afterSchool.getWeekDay());

        TeacherEntity teacher = resolveTeacher(requestDto.teacherId(), afterSchool);
        PlaceEntity place = resolvePlace(requestDto.placeId(), afterSchool);
        WeekDay weekDay = resolveWeekDay(requestDto.weekDay(), afterSchool);
        SchoolPeriod schoolPeriod = resolveSchoolPeriod(requestDto.period(), afterSchool);
        String name = requestDto.name() != null ? requestDto.name() : afterSchool.getName();
        Integer grade = requestDto.grade() != null ? requestDto.grade() : afterSchool.getGrade();
        Integer year = requestDto.year() != null ? requestDto.year() : afterSchool.getYear();

        boolean hasChanges = hasAnyChange(teacher, place, weekDay, schoolPeriod, year, name, grade, afterSchool);

        afterSchool.updateAfterSchool(
                teacher,
                place,
                weekDay,
                schoolPeriod,
                year,
                name,
                grade
        );

        SupervisionBanDayEntity supervisionBanDayEntity = SupervisionBanDayEntity.builder()
                .teacher(teacher)
                .weekDay(weekDay)
                .isAfterschool(true)
                .build();

        supervisionBanDayRepository.save(supervisionBanDayEntity);

        if (hasChanges && isDateInCurrentWeek(LocalDate.now().with(weekDay.toDayOfWeek()))) {
            List<Long> allStudentIds = afterSchool.getAfterSchoolStudents().stream()
                    .map(afterSchoolStudent -> afterSchoolStudent.getStudent().getId())
                    .toList();

            if (!allStudentIds.isEmpty()) {
                List<StudentEntity> allStudents = fetchStudentsByIds(allStudentIds);
                StudentAssignmentResultVo studentAssignmentResultVo =
                        afterSchoolStudentDomainService.assignStudents(afterSchool, allStudents);
                afterSchoolScheduleService.save(List.of(studentAssignmentResultVo));
            }
        } else {
            updateStudentsIfPresent(requestDto.studentsId(), afterSchool);
        }
    }

    @Transactional
    public void updateMergedAfterSchool(AfterSchoolUpdateRequestDto requestDto) {
        List<Long> ids = parseMergedAfterSchoolIds(requestDto.afterSchoolId());

        if (ids.size() != 2) {
            throw new IllegalArgumentException("8~11교시 수정은 2개의 방과후 ID가 필요합니다.");
        }

        AfterSchoolEntity first = getAfterSchoolById(ids.get(0));
        AfterSchoolEntity second = getAfterSchoolById(ids.get(1));

        supervisionBanDayRepository.deleteAfterSchoolBanDay(first.getTeacher().getId(), first.getWeekDay());
        if (!first.getTeacher().getId().equals(second.getTeacher().getId())
                || !first.getWeekDay().equals(second.getWeekDay())) {
            supervisionBanDayRepository.deleteAfterSchoolBanDay(second.getTeacher().getId(), second.getWeekDay());
        }

        TeacherEntity teacher = resolveTeacher(requestDto.teacherId(), first);
        PlaceEntity place = resolvePlace(requestDto.placeId(), first);
        WeekDay weekDay = resolveWeekDay(requestDto.weekDay(), first);
        String name = requestDto.name() != null ? requestDto.name() : first.getName();
        Integer grade = requestDto.grade() != null ? requestDto.grade() : first.getGrade();
        Integer year = requestDto.year() != null ? requestDto.year() : first.getYear();

        boolean firstChanged = hasAnyChange(
                teacher, place, weekDay, SchoolPeriod.EIGHT_AND_NINE_PERIOD, year, name, grade, first
        );
        boolean secondChanged = hasAnyChange(
                teacher, place, weekDay, SchoolPeriod.TEN_AND_ELEVEN_PERIOD, year, name, grade, second
        );

        first.updateAfterSchool(
                teacher,
                place,
                weekDay,
                SchoolPeriod.EIGHT_AND_NINE_PERIOD,
                year,
                name,
                grade
        );

        second.updateAfterSchool(
                teacher,
                place,
                weekDay,
                SchoolPeriod.TEN_AND_ELEVEN_PERIOD,
                year,
                name,
                grade
        );

        SupervisionBanDayEntity supervisionBanDayEntity = SupervisionBanDayEntity.builder()
                .teacher(teacher)
                .weekDay(weekDay)
                .isAfterschool(true)
                .build();

        supervisionBanDayRepository.save(supervisionBanDayEntity);

        if (requestDto.studentsId() != null) {
            List<StudentEntity> students = fetchStudentsByIds(requestDto.studentsId());
            validateStudentsGrade(students, grade);

            StudentAssignmentResultVo firstResult = afterSchoolStudentDomainService.assignStudents(first, students);
            StudentAssignmentResultVo secondResult = afterSchoolStudentDomainService.assignStudents(second, students);
            afterSchoolScheduleService.save(List.of(firstResult, secondResult));
        } else if ((firstChanged || secondChanged) && isDateInCurrentWeek(LocalDate.now().with(weekDay.toDayOfWeek()))) {
            List<StudentAssignmentResultVo> results = new ArrayList<>();

            List<Long> firstStudentIds = first.getAfterSchoolStudents().stream()
                    .map(afterSchoolStudent -> afterSchoolStudent.getStudent().getId())
                    .toList();
            if (!firstStudentIds.isEmpty()) {
                List<StudentEntity> firstStudents = fetchStudentsByIds(firstStudentIds);
                results.add(afterSchoolStudentDomainService.assignStudents(first, firstStudents));
            }

            List<Long> secondStudentIds = second.getAfterSchoolStudents().stream()
                    .map(afterSchoolStudent -> afterSchoolStudent.getStudent().getId())
                    .toList();
            if (!secondStudentIds.isEmpty()) {
                List<StudentEntity> secondStudents = fetchStudentsByIds(secondStudentIds);
                results.add(afterSchoolStudentDomainService.assignStudents(second, secondStudents));
            }

            if (!results.isEmpty()) {
                afterSchoolScheduleService.save(results);
            }
        }
    }

    @Transactional
    public void deleteAfterSchool(Long afterSchoolId) {
        AfterSchoolEntity afterSchool = afterSchoolRepository.findById(afterSchoolId)
                .orElseThrow(() -> new AfterSchoolNotFoundException(afterSchoolId));

        afterSchoolBusinessTripRepository.deleteAllByAfterSchool(afterSchool);
        afterSchoolReinforcementRepository.deleteAllByAfterSchool(afterSchool);
        supervisionBanDayRepository.deleteAfterSchoolBanDay(afterSchool.getTeacher().getId(), afterSchool.getWeekDay());

        afterSchoolRepository.delete(afterSchool);
    }

    @Transactional
    public void quitAfterSchool(Long afterSchoolId) {
        AfterSchoolEntity afterSchool = afterSchoolRepository.findById(afterSchoolId)
                .orElseThrow(() -> new AfterSchoolNotFoundException(afterSchoolId));
        supervisionBanDayRepository.deleteAfterSchoolBanDay(afterSchool.getTeacher().getId(), afterSchool.getWeekDay());
        afterSchool.endAfterSchool();
    }

    @Transactional(readOnly = true)
    public List<AfterSchoolResponseDto> searchAfterSchools(AfterSchoolSearchRequestDto searchRequest) {
        List<AfterSchoolResponseDto> results = afterSchoolRepository.findAfterSchoolsByConditions(searchRequest);
        List<AfterSchoolResponseDto> merged = mergeContinuousPeriodsForSearch(results);
        return merged.stream()
                .sorted(Comparator.comparing(a -> a.teacher().name()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AfterSchoolMyResponseDto> searchMyAfterSchools(Long teacherId, Integer grade) {
        return afterSchoolRepository.findMyAfterSchoolsByTeacherId(teacherId, grade);
    }

    public List<AfterSchoolByTeacherResponseDto> getAfterSchoolsByTeacherId(Long teacherId) {
        List<AfterSchoolEntity> afterSchools = afterSchoolRepository.findByTeacherIdWithRelations(teacherId);

        List<AfterSchoolByTeacherResponseDto> responseList = afterSchools.stream()
                .map(afterSchool -> {
                    int reinforcementCount = afterSchoolReinforcementRepository
                            .findAllByChangeDayBetween(LocalDate.now().minusMonths(1), LocalDate.now().plusDays(1))
                            .stream()
                            .mapToInt(reinforcement -> reinforcement.getAfterSchool().getId().equals(afterSchool.getId()) ? 1 : 0)
                            .sum();

                    return new AfterSchoolByTeacherResponseDto(
                            afterSchool.getId(),
                            afterSchool.getWeekDay().toKorean(),
                            afterSchool.getPeriod().getPeriod(),
                            afterSchool.getName(),
                            new AfterSchoolByTeacherResponseDto.PlaceInfo(
                                    afterSchool.getPlace().getId(),
                                    afterSchool.getPlace().getName()
                            ),
                            reinforcementCount
                    );
                })
                .collect(Collectors.toList());

        return mergeContinuousPeriods(responseList);
    }

    private List<AfterSchoolByTeacherResponseDto> mergeContinuousPeriods(List<AfterSchoolByTeacherResponseDto> responseList) {
        Map<String, List<AfterSchoolByTeacherResponseDto>> groupedByWeekDay = responseList.stream()
                .collect(Collectors.groupingBy(AfterSchoolByTeacherResponseDto::weekDay));

        List<AfterSchoolByTeacherResponseDto> mergedList = new ArrayList<>();

        for (AfterSchoolByTeacherResponseDto dto : responseList) {
            String weekDay = dto.weekDay();
            List<AfterSchoolByTeacherResponseDto> dayGroup = groupedByWeekDay.get(weekDay);

            if (dayGroup == null) continue;

            boolean hasEightNine = dayGroup.stream().anyMatch(d -> "8~9교시".equals(d.period()));
            boolean hasTenEleven = dayGroup.stream().anyMatch(d -> "10~11교시".equals(d.period()));

            if (hasEightNine && hasTenEleven) {
                AfterSchoolByTeacherResponseDto eightNineDto = dayGroup.stream()
                        .filter(d -> "8~9교시".equals(d.period()))
                        .findFirst()
                        .orElse(null);

                AfterSchoolByTeacherResponseDto tenElevenDto = dayGroup.stream()
                        .filter(d -> "10~11교시".equals(d.period()))
                        .findFirst()
                        .orElse(null);

                if (eightNineDto != null && tenElevenDto != null) {
                    AfterSchoolByTeacherResponseDto mergedDto = new AfterSchoolByTeacherResponseDto(
                            eightNineDto.id(),
                            eightNineDto.weekDay(),
                            "8~11교시",
                            eightNineDto.name(),
                            eightNineDto.place(),
                            eightNineDto.reinforcementCount() + tenElevenDto.reinforcementCount()
                    );

                    mergedList.add(mergedDto);

                    dayGroup.stream()
                            .filter(d -> !"8~9교시".equals(d.period()) && !"10~11교시".equals(d.period()))
                            .forEach(mergedList::add);
                } else {
                    mergedList.addAll(dayGroup);
                }
            } else {
                mergedList.add(dto);
            }

            groupedByWeekDay.remove(weekDay);
        }

        return mergedList;
    }

    private List<AfterSchoolResponseDto> mergeContinuousPeriodsForSearch(List<AfterSchoolResponseDto> responseList) {
        Map<String, List<AfterSchoolResponseDto>> groupedByWeekDay = responseList.stream()
                .collect(Collectors.groupingBy(AfterSchoolResponseDto::weekDay));

        List<AfterSchoolResponseDto> mergedList = new ArrayList<>();

        for (Map.Entry<String, List<AfterSchoolResponseDto>> entry : groupedByWeekDay.entrySet()) {
            List<AfterSchoolResponseDto> dayGroup = entry.getValue();
            List<AfterSchoolResponseDto> processedDtos = new ArrayList<>();

            for (AfterSchoolResponseDto dto : dayGroup) {
                if (processedDtos.contains(dto)) continue;

                if ("8~9교시".equals(dto.period())) {
                    AfterSchoolResponseDto pair = findMatchingPeriod(dto, "10~11교시", dayGroup, processedDtos);
                    if (pair != null) {
                        mergedList.add(createMergedDto(dto, pair, dto));
                        processedDtos.add(dto);
                        processedDtos.add(pair);
                    } else {
                        mergedList.add(dto);
                        processedDtos.add(dto);
                    }
                } else if ("10~11교시".equals(dto.period())) {
                    AfterSchoolResponseDto pair = findMatchingPeriod(dto, "8~9교시", dayGroup, processedDtos);
                    if (pair != null) {
                        mergedList.add(createMergedDto(pair, dto, pair));
                        processedDtos.add(dto);
                        processedDtos.add(pair);
                    } else {
                        mergedList.add(dto);
                        processedDtos.add(dto);
                    }
                } else {
                    mergedList.add(dto);
                    processedDtos.add(dto);
                }
            }
        }

        return mergedList;
    }

    private AfterSchoolResponseDto findMatchingPeriod(
            AfterSchoolResponseDto dto,
            String targetPeriod,
            List<AfterSchoolResponseDto> dayGroup,
            List<AfterSchoolResponseDto> processedDtos
    ) {
        return dayGroup.stream()
                .filter(d -> targetPeriod.equals(d.period()) &&
                        d.name().equals(dto.name()) &&
                        d.teacher().id().equals(dto.teacher().id()) &&
                        d.place().id().equals(dto.place().id()) &&
                        hasSameStudents(d.students(), dto.students()) &&
                        !processedDtos.contains(d))
                .findFirst()
                .orElse(null);
    }

    private boolean hasSameStudents(List<StudentInfo> students1, List<StudentInfo> students2) {
        if (students1.size() != students2.size()) {
            return false;
        }

        List<Long> ids1 = students1.stream()
                .map(StudentInfo::id)
                .sorted()
                .toList();
        List<Long> ids2 = students2.stream()
                .map(StudentInfo::id)
                .sorted()
                .toList();

        return ids1.equals(ids2);
    }

    private AfterSchoolResponseDto createMergedDto(
            AfterSchoolResponseDto eightNineDto,
            AfterSchoolResponseDto tenElevenDto,
            AfterSchoolResponseDto baseDto
    ) {
        List<StudentInfo> mergedStudents = new ArrayList<>(eightNineDto.students());
        tenElevenDto.students().forEach(student -> {
            if (!mergedStudents.contains(student)) {
                mergedStudents.add(student);
            }
        });

        String mergedId = eightNineDto.id() + "," + tenElevenDto.id();

        return new AfterSchoolResponseDto(
                mergedId,
                baseDto.weekDay(),
                "8~11교시",
                baseDto.name(),
                baseDto.teacher(),
                baseDto.place(),
                mergedStudents
        );
    }

    @Transactional(readOnly = true)
    public List<AfterSchoolTodayResponseDto> searchMyTodayAfterSchoolsToday(Long teacherId) {
        return afterSchoolRepository.findMyTodayAfterSchoolsByTeacherId(teacherId);
    }

    private List<StudentEntity> fetchStudentsByIds(List<Long> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            return List.of();
        }

        List<StudentEntity> students = studentRepository.findAllById(studentIds);

        if (students.size() != studentIds.size()) {
            throw new StudentNotFoundException();
        }

        return students;
    }

    private TeacherEntity getTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(TeacherNotFoundException::new);
    }

    private PlaceEntity getPlaceById(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(PlaceNotFoundException::new);
    }

    private BranchEntity getBranchByYearAndId(Integer year, Integer branch) {
        return branchRepository.findByYearAndBranch(year, branch)
                .orElseThrow(BranchNotFoundException::new);
    }

    private AfterSchoolEntity getAfterSchoolById(Long id) {
        return afterSchoolRepository.findWithAllRelations(id)
                .orElseThrow(() -> new AfterSchoolNotFoundException(id));
    }

    private TeacherEntity resolveTeacher(Long teacherId, AfterSchoolEntity afterSchool) {
        return teacherId != null ? getTeacherById(teacherId) : afterSchool.getTeacher();
    }

    private PlaceEntity resolvePlace(Long placeId, AfterSchoolEntity afterSchool) {
        return placeId != null ? getPlaceById(placeId) : afterSchool.getPlace();
    }

    private WeekDay resolveWeekDay(WeekDay weekDay, AfterSchoolEntity afterSchool) {
        return weekDay != null ? weekDay : afterSchool.getWeekDay();
    }

    private SchoolPeriod resolveSchoolPeriod(String period, AfterSchoolEntity afterSchool) {
        if (period == null || period.isBlank()) {
            return afterSchool.getPeriod();
        }

        return switch (period) {
            case "SEVEN_PERIOD", "7교시" -> SchoolPeriod.SEVEN_PERIOD;
            case "EIGHT_AND_NINE_PERIOD", "8~9교시" -> SchoolPeriod.EIGHT_AND_NINE_PERIOD;
            case "TEN_AND_ELEVEN_PERIOD", "10~11교시" -> SchoolPeriod.TEN_AND_ELEVEN_PERIOD;
            default -> throw new IllegalArgumentException("지원하지 않는 교시입니다: " + period);
        };
    }

    private boolean isMergedUpdateRequest(AfterSchoolUpdateRequestDto requestDto) {
        return (requestDto.afterSchoolId() != null && requestDto.afterSchoolId().contains(","))
                || "8~11교시".equals(requestDto.period());
    }

    private Long parseSingleAfterSchoolId(String afterSchoolId) {
        try {
            return Long.parseLong(afterSchoolId.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("단일 방과후 ID 형식이 잘못되었습니다: " + afterSchoolId);
        }
    }

    private List<Long> parseMergedAfterSchoolIds(String afterSchoolId) {
        try {
            return Arrays.stream(afterSchoolId.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .toList();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("묶음 방과후 ID 형식이 잘못되었습니다: " + afterSchoolId);
        }
    }

    private void updateStudentsIfPresent(List<Long> studentIds, AfterSchoolEntity afterSchool) {
        if (studentIds == null) return;
        List<StudentEntity> students = fetchStudentsByIds(studentIds);
        validateStudentsGrade(students, afterSchool.getGrade());

        StudentAssignmentResultVo studentAssignmentResultVo = afterSchoolStudentDomainService.assignStudents(
                afterSchool,
                students
        );
        afterSchoolScheduleService.save(List.of(studentAssignmentResultVo));
    }

    @Transactional
    public void createBusinessTrip(AfterSchoolBusinessTripRequestDto requestDto) {
        AfterSchoolEntity afterSchool = getAfterSchoolById(requestDto.afterschoolId());

        AfterSchoolBusinessTripEntity businessTrip = AfterSchoolBusinessTripEntity.builder()
                .day(requestDto.day())
                .afterSchool(afterSchool)
                .build();

        afterSchoolBusinessTripRepository.save(businessTrip);

        if (isDateInCurrentWeek(requestDto.day())) {
            deleteRecentAfterSchoolSchedules(afterSchool, requestDto.day());
        }
    }

    @Transactional
    public void createReinforcement(AfterSchoolReinforcementRequestDto requestDto) {
        AfterSchoolEntity afterSchool = getAfterSchoolById(requestDto.afterschoolId());
        PlaceEntity changePlace = getPlaceById(requestDto.changePlaceId());
        if (placeRepository.existAfterSchoolPlaceByDayAndPeriodAndPlace(requestDto.day(), requestDto.changePeriod(), changePlace)) {
            throw new PlaceAlreadyBookedException();
        }

        AfterSchoolReinforcementEntity reinforcement = AfterSchoolReinforcementEntity.builder()
                .changeDay(requestDto.day())
                .afterSchool(afterSchool)
                .changePeriod(requestDto.changePeriod())
                .place(changePlace)
                .build();

        afterSchoolReinforcementRepository.save(reinforcement);

        if (isDateInCurrentWeek(requestDto.day())) {
            createAfterSchoolReinforcementSchedules(afterSchool, requestDto.day(), requestDto.changePeriod());
        }
    }

    @Transactional(readOnly = true)
    public AfterSchoolAffordableBusinessResponseDto getBusinessTrip(Long afterSchoolId) {
        LocalDate now = LocalDate.now();
        BranchEntity branchEntity = branchRepository.findCurrentBranch(now).orElseThrow(BranchNotFoundException::new);
        LocalDate startDay = branchEntity.getStartDay();
        LocalDate afterSchoolEndDay = branchEntity.getAfterSchoolEndDay();
        AfterSchoolEntity afterSchool = getAfterSchoolById(afterSchoolId);

        List<LocalDate> existingBusinessTripDates = afterSchoolBusinessTripRepository
                .findBusinessTripDatesByAfterSchoolAndDateRange(afterSchool, startDay, afterSchoolEndDay);

        List<LocalDate> localDates = new ArrayList<>();
        DayOfWeek targetDayOfWeek = afterSchool.getWeekDay().toDayOfWeek();

        for (LocalDate day = startDay; day.isBefore(afterSchoolEndDay); day = day.plusDays(1)) {
            if (!day.getDayOfWeek().equals(targetDayOfWeek)) continue;
            if (existingBusinessTripDates.contains(day)) continue;
            localDates.add(day);
        }

        return AfterSchoolAffordableBusinessResponseDto.builder()
                .dates(localDates)
                .build();
    }

    private boolean isDateInCurrentWeek(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        return !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek);
    }

    private void deleteRecentAfterSchoolSchedules(AfterSchoolEntity afterSchool, LocalDate businessTripDay) {
        log.info("=== 출장 스케줄 삭제 시작 ===");
        log.info("방과후: {}, 출장날짜: {}, 교시: {}", afterSchool.getName(), businessTripDay, afterSchool.getPeriod());

        List<StudentScheduleEntity> afterSchoolSchedules = studentScheduleRepository
                .findAllByAfterSchoolAndDayAndPeriod(afterSchool, businessTripDay, afterSchool.getPeriod());

        log.info("찾은 StudentSchedule 수: {}", afterSchoolSchedules.size());

        List<Long> studentScheduleIds = afterSchoolSchedules.stream()
                .map(StudentScheduleEntity::getId)
                .toList();

        log.info("StudentSchedule IDs: {}", studentScheduleIds);

        if (studentScheduleIds.isEmpty()) {
            log.info("StudentSchedule이 없어서 예외 발생");
            throw new AfterSchoolBusinessTripScheduleNotFoundException(afterSchool.getName());
        }

        List<Long> scheduleIds = scheduleRepository.findTopScheduleIdsByStudentScheduleIds(studentScheduleIds, ScheduleType.AFTER_SCHOOL);

        log.info("삭제할 Schedule IDs: {}", scheduleIds);

        if (!scheduleIds.isEmpty()) {
            afterSchoolScheduleRepository.deleteByScheduleIds(scheduleIds);
            scheduleRepository.deleteByIds(scheduleIds);
            log.info("스케줄 삭제 완료");
        } else {
            log.info("삭제할 방과후 타입 스케줄이 없음");
        }
        log.info("=== 출장 스케줄 삭제 끝 ===");
    }

    private boolean hasAnyChange(
            TeacherEntity teacher,
            PlaceEntity place,
            WeekDay weekDay,
            SchoolPeriod schoolPeriod,
            Integer year,
            String name,
            Integer grade,
            AfterSchoolEntity afterSchool
    ) {
        return !teacher.equals(afterSchool.getTeacher()) ||
                !place.equals(afterSchool.getPlace()) ||
                !weekDay.equals(afterSchool.getWeekDay()) ||
                !schoolPeriod.equals(afterSchool.getPeriod()) ||
                !year.equals(afterSchool.getYear()) ||
                !name.equals(afterSchool.getName()) ||
                !grade.equals(afterSchool.getGrade());
    }

    private void validateStudentsGrade(List<StudentEntity> students, Integer requiredGrade) {
        List<StudentEntity> invalidGradeStudents = students.stream()
                .filter(student -> !student.getGrade().equals(requiredGrade))
                .toList();

        if (!invalidGradeStudents.isEmpty()) {
            throw new InvalidStudentInfoException("방과후 수업 학년과 일치하지 않는 학생이 포함되어 있습니다.");
        }
    }

    private void createAfterSchoolReinforcementSchedules(
            AfterSchoolEntity afterSchool,
            LocalDate reinforcementDay,
            SchoolPeriod reinforcementPeriod
    ) {
        log.info("=== 보강 스케줄 생성 시작 ===");
        log.info("방과후: {}, 보강날짜: {}, 보강교시: {}", afterSchool.getName(), reinforcementDay, reinforcementPeriod);

        List<StudentEntity> afterSchoolStudents = afterSchool.getAfterSchoolStudents().stream()
                .map(AfterSchoolStudentEntity::getStudent)
                .toList();

        log.info("방과후를 듣는 학생 수: {}", afterSchoolStudents.size());

        List<StudentScheduleEntity> reinforcementStudentSchedules = afterSchoolStudents.stream()
                .map(student -> StudentScheduleEntity.builder()
                        .student(student)
                        .day(reinforcementDay)
                        .period(reinforcementPeriod)
                        .build())
                .toList();

        log.info("생성할 StudentSchedule 수: {}", reinforcementStudentSchedules.size());

        studentScheduleRepository.saveAll(reinforcementStudentSchedules);
        log.info("StudentSchedule 저장 완료");

        List<ScheduleEntity> reinforcementSchedules = reinforcementStudentSchedules.stream()
                .map(studentSchedule -> {
                    Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
                    log.debug("학생 {}, StudentSchedule ID: {}, 마지막 stackOrder: {}",
                            studentSchedule.getStudent().getName(), studentSchedule.getId(), lastStackOrder);
                    return ScheduleEntity.createNewStudentSchedule(
                            studentSchedule,
                            lastStackOrder,
                            ScheduleType.AFTER_SCHOOL_REINFORCEMENT
                    );
                })
                .toList();

        log.info("생성할 보강 Schedule 수: {}", reinforcementSchedules.size());

        scheduleRepository.saveAll(reinforcementSchedules);
        log.info("보강 스케줄 저장 완료");

        log.info("=== 보강 스케줄 생성 끝 ===");
    }
}
