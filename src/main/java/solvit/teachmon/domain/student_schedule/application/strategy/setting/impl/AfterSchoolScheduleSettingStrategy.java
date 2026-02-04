package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AfterSchoolScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AfterSchoolScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AfterSchoolScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final ScheduleRepository scheduleRepository;
    private final AfterSchoolRepository afterSchoolRepository;
    private final AfterSchoolScheduleRepository afterSchoolScheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final BranchRepository branchRepository;
    private final AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.AFTER_SCHOOL;
    }

    @Override
    public void settingSchedule() {
        BranchEntity branch = branchRepository.findByDay(LocalDate.now())
                .orElseThrow(BranchNotFoundException::new);

        List<AfterSchoolEntity> afterSchools = afterSchoolRepository.findAllByBranch(branch);

        for(AfterSchoolEntity afterSchool : afterSchools) {
            // 출장이면 넘어가기
            if(afterSchoolBusinessTripRepository.existsByAfterSchoolAndDay(afterSchool, calculateAfterSchoolDay(afterSchool)))
                continue;
            List<StudentScheduleEntity> studentSchedules = findStudentScheduleByAfterSchool(afterSchool);
            settingAfterSchoolSchedule(studentSchedules, afterSchool);
        }
    }

    private List<StudentScheduleEntity> findStudentScheduleByAfterSchool(AfterSchoolEntity afterSchool) {
        return studentScheduleRepository.findAllByAfterSchoolAndDayAndPeriod(
                afterSchool, calculateAfterSchoolDay(afterSchool), afterSchool.getPeriod()
        );
    }

    private LocalDate calculateAfterSchoolDay(AfterSchoolEntity afterSchool) {
        LocalDate today = LocalDate.now();
        return today.with(afterSchool.getWeekDay().toDayOfWeek()).plusWeeks(1);
    }

    private void settingAfterSchoolSchedule(
            List<StudentScheduleEntity> studentSchedules,
            AfterSchoolEntity afterSchool
    ) {
        for(StudentScheduleEntity studentSchedule : studentSchedules) {
            ScheduleEntity newSchedule = createNewSchedule(studentSchedule);
            createAfterSchoolSchedule(newSchedule, afterSchool);
        }
    }

    private void createAfterSchoolSchedule(
            ScheduleEntity schedule,
            AfterSchoolEntity afterSchool
    ) {
        AfterSchoolScheduleEntity afterSchoolSchedule = AfterSchoolScheduleEntity.builder()
                .schedule(schedule)
                .afterSchool(afterSchool)
                .build();

        afterSchoolScheduleRepository.save(afterSchoolSchedule);
    }

    private ScheduleEntity createNewSchedule(StudentScheduleEntity studentSchedule) {
        // 새로운 스케줄 생성
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(studentSchedule, lastStackOrder, ScheduleType.AFTER_SCHOOL);

        scheduleRepository.save(newSchedule);

        return newSchedule;
    }
}
