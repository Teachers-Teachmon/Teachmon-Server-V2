package solvit.teachmon.domain.student_schedule.application.batch.processor;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.student_schedule.application.batch.dto.SelfStudyScheduleDto;
import solvit.teachmon.domain.student_schedule.application.batch.dto.StudentScheduleInfo;
import solvit.teachmon.domain.student_schedule.application.batch.stepscope.PlacesByGradeMapHolder;
import solvit.teachmon.domain.student_schedule.application.batch.stepscope.StudentScheduleInfoMapHolder;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.SelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.exception.NoAvailablePlaceException;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static solvit.teachmon.domain.place.domain.entity.PlaceEntity.calculateNextClassNumber;

/**
 * SELF_STUDY Processor — N+1 완전 제거
 *
 * - stackOrderMap    : ExecutionContext (ConcurrentHashMap, thread-safe)
 * - occupiedPlaceSet : ExecutionContext ("day_period_placeId" Set) — checkPlaceAvailability 대체
 * - studentScheduleMap: Step 초기화 시 주간 bulk 조회
 * - placesByGradeMap  : Step 초기화 시 전체 bulk 조회
 */
@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class SelfStudyScheduleProcessor implements ItemProcessor<SelfStudyEntity, List<SelfStudyScheduleDto>> {
    private final StudentScheduleRepository studentScheduleRepository;
    private final PlaceRepository placeRepository;
    private final EntityManager entityManager;
    private final StudentScheduleInfoMapHolder studentScheduleInfoMapHolder;
    private final PlacesByGradeMapHolder placesByGradeMapHolder;

    @Value("#{jobParameters['baseDate']}")
    private LocalDate baseDate;

    @Value("#{jobExecutionContext['stackOrderMap']}")
    private ConcurrentHashMap<Long, AtomicInteger> stackOrderMap;

    @Value("#{jobExecutionContext['occupiedPlaceSet']}")
    private Set<String> occupiedPlaceSet;

    // Step 실행 시 한 번만 조회
    private Map<String, List<StudentScheduleInfo>> studentScheduleMap;
    private Map<Integer, Map<Integer, PlaceEntity>> placesByGradeMap;

    @Override
    public List<SelfStudyScheduleDto> process(SelfStudyEntity selfStudy) {
        ensureInitialized();

        LocalDate selfStudyDay = baseDate.with(selfStudy.getWeekDay().toDayOfWeek());
        String key = selfStudy.getGrade() + "_" + selfStudyDay + "_" + selfStudy.getPeriod();
        List<StudentScheduleInfo> infos = studentScheduleMap.getOrDefault(key, List.of());

        List<SelfStudyScheduleDto> results = new ArrayList<>();

        for (StudentScheduleInfo info : infos) {
            ScheduleEntity schedule = createNewSchedule(info);
            PlaceEntity place = findSelfStudyPlace(info);

            StudentScheduleEntity ref = entityManager.getReference(StudentScheduleEntity.class, info.id());
            SelfStudyScheduleEntity selfStudySchedule = SelfStudyScheduleEntity.builder()
                    .schedule(schedule)
                    .place(place)
                    .selfStudy(selfStudy)
                    .build();

            results.add(new SelfStudyScheduleDto(schedule, selfStudySchedule));
        }

        return results;
    }

    private void ensureInitialized() {
        if (studentScheduleMap != null) return;

        Map<String, List<StudentScheduleInfo>> cached = studentScheduleInfoMapHolder != null
                ? studentScheduleInfoMapHolder.get()
                : Map.of();
        if (!cached.isEmpty()) {
            studentScheduleMap = cached;
        } else {
            LocalDate weekStart = baseDate.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = baseDate.with(DayOfWeek.SUNDAY);
            Map<String, List<StudentScheduleEntity>> entityMap =
                    studentScheduleRepository.findAllByDayBetween(weekStart, weekEnd)
                            .stream()
                            .collect(Collectors.groupingBy(ss ->
                                    ss.getStudent().getGrade() + "_" + ss.getDay() + "_" + ss.getPeriod()
                            ));
            studentScheduleMap = new HashMap<>();
            for (var entry : entityMap.entrySet()) {
                List<StudentScheduleInfo> infos = entry.getValue().stream()
                        .map(ss -> new StudentScheduleInfo(
                                ss.getId(), ss.getStudent().getGrade(), ss.getStudent().getClassNumber(), ss.getDay(), ss.getPeriod()))
                        .toList();
                studentScheduleMap.put(entry.getKey(), infos);
            }
        }

        if (placesByGradeMapHolder != null) {
            placesByGradeMap = placesByGradeMapHolder.get();
        } else {
            placesByGradeMap = new HashMap<>();
            for (PlaceEntity place : placeRepository.findAll()) {
                String name = place.getName();
                if (!name.contains("-")) continue;
                try {
                    int grade = Integer.parseInt(name.substring(0, name.indexOf('-')));
                    int classNum = Integer.parseInt(name.substring(name.indexOf('-') + 1));
                    placesByGradeMap.computeIfAbsent(grade, k -> new HashMap<>()).put(classNum, place);
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private ScheduleEntity createNewSchedule(StudentScheduleInfo info) {
        int stackOrder = stackOrderMap
                .computeIfAbsent(info.id(), id -> new AtomicInteger(0))
                .getAndIncrement();
        StudentScheduleEntity ref = entityManager.getReference(StudentScheduleEntity.class, info.id());
        return ScheduleEntity.createNewStudentSchedule(ref, stackOrder, ScheduleType.SELF_STUDY);
    }

    private PlaceEntity findSelfStudyPlace(StudentScheduleInfo info) {
        Map<Integer, PlaceEntity> placesMap = placesByGradeMap.get(info.grade());

        if (placesMap == null) {
            placesMap = placeRepository.findAllByGradePrefix(info.grade());
        }

        Integer targetPoint = info.classNumber();
        for (int count = 0; count < 4; count++) {
            PlaceEntity place = placesMap.get(targetPoint);

            if (place != null && isPlaceAvailable(info.day(), info.period(), place)) {
                return place;
            }
            targetPoint = calculateNextClassNumber(targetPoint);
        }

        throw new NoAvailablePlaceException();
    }

    /** occupiedPlaceSet 기반 O(1) place 가용성 확인 — checkPlaceAvailability N+1 완전 대체 */
    private boolean isPlaceAvailable(LocalDate day, solvit.teachmon.global.enums.SchoolPeriod period, PlaceEntity place) {
        return !occupiedPlaceSet.contains(day + "_" + period + "_" + place.getId());
    }
}
