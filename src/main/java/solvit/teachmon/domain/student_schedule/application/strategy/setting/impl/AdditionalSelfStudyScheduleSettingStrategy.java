package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleGenerator;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AdditionalSelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.exception.NoAvailablePlaceException;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AdditionalSelfStudyScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static solvit.teachmon.domain.place.domain.entity.PlaceEntity.calculateNextClassNumber;

@Component
@RequiredArgsConstructor
public class AdditionalSelfStudyScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final AdditionalSelfStudyRepository additionalSelfStudyRepository;
    private final ScheduleRepository scheduleRepository;
    private final AdditionalSelfStudyScheduleRepository additionalSelfStudyScheduleRepository;
    private final PlaceRepository placeRepository;
    private final StudentScheduleGenerator studentScheduleGenerator;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.ADDITIONAL_SELF_STUDY;
    }

    @Override
    public void settingSchedule(LocalDate baseDate) {
        settingSchedule(baseDate, new ConcurrentHashMap<>());
    }

    public void settingSchedule(LocalDate baseDate, ConcurrentHashMap<Long, AtomicInteger> stackOrderMap) {
        settingSchedule(baseDate, stackOrderMap, java.util.Collections.emptySet());
    }

    public void settingSchedule(
            LocalDate baseDate,
            ConcurrentHashMap<Long, AtomicInteger> stackOrderMap,
            Set<String> occupiedPlaceSet
    ) {
        List<AdditionalSelfStudyEntity> additionalSelfStudies = findWeeklyAdditionalSelfStudies(baseDate);

        // 전체 places를 한 번에 조회 → grade별 Map으로 구성 (findAllByGradePrefix N+1 제거)
        Map<Integer, Map<Integer, PlaceEntity>> placesByGradeMap = buildPlacesByGradeMap();

        List<ScheduleEntity> schedulesToSave = new ArrayList<>();
        List<AdditionalSelfStudyScheduleEntity> additionalSelfStudySchedulesToSave = new ArrayList<>();

        for (AdditionalSelfStudyEntity additionalSelfStudy : additionalSelfStudies) {
            if (additionalSelfStudy.getDay().isBefore(baseDate))
                continue;

            List<StudentScheduleEntity> studentSchedules = studentScheduleGenerator.findOrCreateStudentSchedules(
                    additionalSelfStudy.getGrade(),
                    additionalSelfStudy.getDay(),
                    additionalSelfStudy.getPeriod()
            );

            for (StudentScheduleEntity studentSchedule : studentSchedules) {
                PlaceEntity place = findAdditionalSelfStudyPlace(studentSchedule, placesByGradeMap, occupiedPlaceSet);
                int stackOrder = stackOrderMap
                        .computeIfAbsent(studentSchedule.getId(), id -> new AtomicInteger(0))
                        .getAndIncrement();
                ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(
                        studentSchedule, stackOrder, ScheduleType.ADDITIONAL_SELF_STUDY
                );
                schedulesToSave.add(newSchedule);
                additionalSelfStudySchedulesToSave.add(AdditionalSelfStudyScheduleEntity.builder()
                        .schedule(newSchedule)
                        .place(place)
                        .additionalSelfStudy(additionalSelfStudy)
                        .build());
            }
        }

        scheduleRepository.saveAll(schedulesToSave);
        additionalSelfStudyScheduleRepository.saveAll(additionalSelfStudySchedulesToSave);
    }

    private Map<Integer, Map<Integer, PlaceEntity>> buildPlacesByGradeMap() {
        Map<Integer, Map<Integer, PlaceEntity>> result = new HashMap<>();
        for (PlaceEntity place : placeRepository.findAll()) {
            String name = place.getName();
            if (!name.contains("-")) continue;
            try {
                int grade = Integer.parseInt(name.substring(0, name.indexOf('-')));
                int classNum = Integer.parseInt(name.substring(name.indexOf('-') + 1));
                result.computeIfAbsent(grade, k -> new HashMap<>()).put(classNum, place);
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }

    private List<AdditionalSelfStudyEntity> findWeeklyAdditionalSelfStudies(LocalDate baseDate) {
        LocalDate startDay = baseDate.with(DayOfWeek.MONDAY);
        LocalDate endDay = baseDate.with(DayOfWeek.SUNDAY);
        return additionalSelfStudyRepository.findAllByDayBetween(startDay, endDay);
    }

    private PlaceEntity findAdditionalSelfStudyPlace(
            StudentScheduleEntity studentSchedule,
            Map<Integer, Map<Integer, PlaceEntity>> placesByGradeMap,
            Set<String> occupiedPlaceSet
    ) {
        StudentEntity student = studentSchedule.getStudent();
        Map<Integer, PlaceEntity> placesMap = placesByGradeMap.get(student.getGrade());
        if (placesMap == null) {
            placesMap = placeRepository.findAllByGradePrefix(student.getGrade());
        }

        Integer targetPoint = student.getClassNumber();
        for (int count = 0; count < 4; count++) {
            PlaceEntity place = placesMap.get(targetPoint);

            if (place == null) {
                targetPoint = calculateNextClassNumber(targetPoint);
                continue;
            }

            boolean available = occupiedPlaceSet.isEmpty()
                    ? !placeRepository.checkPlaceAvailability(studentSchedule.getDay(), studentSchedule.getPeriod(), place)
                    : !occupiedPlaceSet.contains(studentSchedule.getDay() + "_" + studentSchedule.getPeriod() + "_" + place.getId());
            if (available)
                return place;
            targetPoint = calculateNextClassNumber(targetPoint);
        }

        throw new NoAvailablePlaceException();
    }
}
