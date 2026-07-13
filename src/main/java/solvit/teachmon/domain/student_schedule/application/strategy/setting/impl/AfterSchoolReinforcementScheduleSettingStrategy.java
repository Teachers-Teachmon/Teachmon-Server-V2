package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.student_schedule.application.batch.dto.StudentScheduleInfo;
import solvit.teachmon.domain.student_schedule.application.batch.stepscope.StudentScheduleInfoMapHolder;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AfterSchoolScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AfterSchoolScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class AfterSchoolReinforcementScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final ScheduleRepository scheduleRepository;
    private final AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;
    private final AfterSchoolScheduleRepository afterSchoolScheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final EntityManager entityManager;
    private final StudentScheduleInfoMapHolder studentScheduleInfoMapHolder;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.AFTER_SCHOOL_REINFORCEMENT;
    }

    @Override
    public void settingSchedule(LocalDate baseDate) {
        settingSchedule(baseDate, new ConcurrentHashMap<>());
    }

    public void settingSchedule(LocalDate baseDate, ConcurrentHashMap<Long, AtomicInteger> stackOrderMap) {
        settingSchedule(baseDate, stackOrderMap, null);
    }

    public void settingSchedule(
            LocalDate baseDate,
            ConcurrentHashMap<Long, AtomicInteger> stackOrderMap,
            Map<String, List<StudentScheduleInfo>> preloadedStudentScheduleMap
    ) {
        List<AfterSchoolReinforcementEntity> afterSchoolReinforcements = findWeeklyAfterSchoolReinforcements(baseDate);

        Map<String, List<StudentScheduleInfo>> studentScheduleMap;
        if (preloadedStudentScheduleMap != null) {
            studentScheduleMap = preloadedStudentScheduleMap;
        } else {
            studentScheduleMap = loadOrGetStudentScheduleMap(baseDate);
        }

        List<ScheduleEntity> schedulesToSave = new ArrayList<>();
        List<AfterSchoolScheduleEntity> afterSchoolSchedulesToSave = new ArrayList<>();

        for (AfterSchoolReinforcementEntity reinforcement : afterSchoolReinforcements) {
            if (reinforcement.getChangeDay().isBefore(baseDate))
                continue;

            AfterSchoolEntity afterSchool = reinforcement.getAfterSchool();
            String key = afterSchool.getGrade() + "_" + reinforcement.getChangeDay() + "_" + reinforcement.getChangePeriod();
            List<StudentScheduleInfo> infos = studentScheduleMap.getOrDefault(key, Collections.emptyList());

            for (StudentScheduleInfo info : infos) {
                int stackOrder = stackOrderMap
                        .computeIfAbsent(info.id(), id -> new AtomicInteger(0))
                        .getAndIncrement();
                StudentScheduleEntity ref = entityManager.getReference(StudentScheduleEntity.class, info.id());
                ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(
                        ref, stackOrder, ScheduleType.AFTER_SCHOOL_REINFORCEMENT
                );
                schedulesToSave.add(newSchedule);
                afterSchoolSchedulesToSave.add(AfterSchoolScheduleEntity.builder()
                        .schedule(newSchedule)
                        .afterSchool(afterSchool)
                        .build());
            }
        }

        scheduleRepository.saveAll(schedulesToSave);
        afterSchoolScheduleRepository.saveAll(afterSchoolSchedulesToSave);
    }

    private List<AfterSchoolReinforcementEntity> findWeeklyAfterSchoolReinforcements(LocalDate baseDate) {
        LocalDate startDay = baseDate.with(DayOfWeek.MONDAY);
        LocalDate endDay = baseDate.with(DayOfWeek.SUNDAY);
        return afterSchoolReinforcementRepository.findAllByChangeDayBetween(startDay, endDay);
    }

    private Map<String, List<StudentScheduleInfo>> loadOrGetStudentScheduleMap(LocalDate baseDate) {
        if (studentScheduleInfoMapHolder != null) {
            Map<String, List<StudentScheduleInfo>> cached = studentScheduleInfoMapHolder.get();
            if (!cached.isEmpty()) {
                return cached;
            }
        }
        LocalDate weekStart = baseDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = baseDate.with(DayOfWeek.SUNDAY);
        return studentScheduleRepository.findAllByDayBetween(weekStart, weekEnd)
                .stream()
                .collect(java.util.stream.Collectors.groupingBy(ss ->
                        ss.getStudent().getGrade() + "_" + ss.getDay() + "_" + ss.getPeriod(),
                        java.util.stream.Collectors.mapping(
                                ss -> new StudentScheduleInfo(ss.getId(), ss.getStudent().getGrade(), ss.getStudent().getClassNumber(), ss.getDay(), ss.getPeriod()),
                                java.util.stream.Collectors.toList()
                        )
                ));
    }
}
