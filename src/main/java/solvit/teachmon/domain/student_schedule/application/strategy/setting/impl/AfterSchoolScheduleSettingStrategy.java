package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.persistence.EntityManager;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AfterSchoolScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final ScheduleRepository scheduleRepository;
    private final AfterSchoolRepository afterSchoolRepository;
    private final AfterSchoolScheduleRepository afterSchoolScheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final BranchRepository branchRepository;
    private final AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;
    private final EntityManager entityManager;
    private final StudentScheduleInfoMapHolder studentScheduleInfoMapHolder;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.AFTER_SCHOOL;
    }

    @Override
    public void settingSchedule(LocalDate baseDate) {
        settingSchedule(baseDate, new ConcurrentHashMap<>(), null, null);
    }

    public void settingSchedule(LocalDate baseDate, ConcurrentHashMap<Long, AtomicInteger> stackOrderMap) {
        settingSchedule(baseDate, stackOrderMap, null, null);
    }

    public void settingSchedule(
            LocalDate baseDate,
            ConcurrentHashMap<Long, AtomicInteger> stackOrderMap,
            Set<String> businessTripSet
    ) {
        settingSchedule(baseDate, stackOrderMap, businessTripSet, null);
    }

    public void settingSchedule(
            LocalDate baseDate,
            ConcurrentHashMap<Long, AtomicInteger> stackOrderMap,
            Set<String> businessTripSet,
            Map<String, List<StudentScheduleInfo>> preloadedStudentScheduleMap
    ) {
        BranchEntity branch = branchRepository.findByDay(baseDate)
                .orElseThrow(BranchNotFoundException::new);

        List<AfterSchoolEntity> afterSchools = afterSchoolRepository.findAllByBranch(branch);

        Map<String, List<StudentScheduleInfo>> studentScheduleMap;
        if (preloadedStudentScheduleMap != null) {
            studentScheduleMap = preloadedStudentScheduleMap;
        } else {
            studentScheduleMap = loadOrGetStudentScheduleMap(baseDate);
        }

        List<ScheduleEntity> schedulesToSave = new ArrayList<>();
        List<AfterSchoolScheduleEntity> afterSchoolSchedulesToSave = new ArrayList<>();

        for (AfterSchoolEntity afterSchool : afterSchools) {
            if (afterSchool.getIsEnd())
                continue;

            LocalDate afterSchoolDay = baseDate.with(afterSchool.getWeekDay().toDayOfWeek());

            if (isBusinessTrip(afterSchool, afterSchoolDay, businessTripSet))
                continue;

            if (afterSchoolDay.isBefore(baseDate))
                continue;

            String key = afterSchool.getGrade() + "_" + afterSchoolDay + "_" + afterSchool.getPeriod();
            List<StudentScheduleInfo> studentSchedules = studentScheduleMap.getOrDefault(key, Collections.emptyList());

            for (StudentScheduleInfo info : studentSchedules) {
                int stackOrder = stackOrderMap
                        .computeIfAbsent(info.id(), id -> new AtomicInteger(0))
                        .getAndIncrement();
                StudentScheduleEntity ref = entityManager.getReference(StudentScheduleEntity.class, info.id());
                ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(
                        ref, stackOrder, ScheduleType.AFTER_SCHOOL
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

    /** businessTripSet이 null이면 DB fallback, null이 아니면(빈 Set 포함) 사전로딩 데이터 사용 */
    private boolean isBusinessTrip(AfterSchoolEntity afterSchool, LocalDate day, Set<String> businessTripSet) {
        if (businessTripSet != null) {
            return businessTripSet.contains(afterSchool.getId() + "_" + day);
        }
        return afterSchoolBusinessTripRepository.existsByAfterSchoolAndDay(afterSchool, day);
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
                .collect(Collectors.groupingBy(ss ->
                        ss.getStudent().getGrade() + "_" + ss.getDay() + "_" + ss.getPeriod(),
                        Collectors.mapping(
                                ss -> new StudentScheduleInfo(ss.getId(), ss.getStudent().getGrade(), ss.getStudent().getClassNumber(), ss.getDay(), ss.getPeriod()),
                                Collectors.toList()
                        )
                ));
    }
}
