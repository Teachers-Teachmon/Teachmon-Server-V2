package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.ExitScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ExitRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.ExitScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExitScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final ExitRepository exitRepository;
    private final ExitScheduleRepository exitScheduleRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.EXIT;
    }

    @Override
    public void settingSchedule(LocalDate baseDate) {
        List<ExitEntity> exits = exitRepository.findAllFromDate(baseDate);

        for (ExitEntity exit : exits) {
            studentScheduleRepository
                    .findByStudentAndDayAndPeriod(exit.getStudent(), exit.getDay(), exit.getPeriod())
                    .ifPresent(studentSchedule -> {
                        ScheduleEntity newSchedule = createNewSchedule(studentSchedule);
                        createExitSchedule(newSchedule, exit);
                    });
        }
    }

    private ScheduleEntity createNewSchedule(StudentScheduleEntity studentSchedule) {
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(
                studentSchedule, lastStackOrder, ScheduleType.EXIT);
        return scheduleRepository.save(newSchedule);
    }

    private void createExitSchedule(ScheduleEntity schedule, ExitEntity exit) {
        ExitScheduleEntity exitSchedule = ExitScheduleEntity.builder()
                .schedule(schedule)
                .exit(exit)
                .build();
        exitScheduleRepository.save(exitSchedule);
    }
}
