package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.AwayEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AwayScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.AwayRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AwayScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AwayScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final AwayRepository awayRepository;
    private final AwayScheduleRepository awayScheduleRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.AWAY;
    }

    @Override
    public void settingSchedule(LocalDate baseDate) {
        List<AwayEntity> aways = awayRepository.findAllFromDate(baseDate);

        for (AwayEntity away : aways) {
            studentScheduleRepository
                    .findByStudentAndDayAndPeriod(away.getStudent(), away.getDay(), away.getPeriod())
                    .ifPresent(studentSchedule -> {
                        ScheduleEntity newSchedule = createNewSchedule(studentSchedule);
                        createAwaySchedule(newSchedule, away);
                    });
        }
    }

    private ScheduleEntity createNewSchedule(StudentScheduleEntity studentSchedule) {
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(
                studentSchedule, lastStackOrder, ScheduleType.AWAY);
        return scheduleRepository.save(newSchedule);
    }

    private void createAwaySchedule(ScheduleEntity schedule, AwayEntity away) {
        AwayScheduleEntity awaySchedule = AwayScheduleEntity.builder()
                .schedule(schedule)
                .away(away)
                .build();
        awayScheduleRepository.save(awaySchedule);
    }
}
