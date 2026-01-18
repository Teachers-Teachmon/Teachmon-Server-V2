package solvit.teachmon.domain.student_schedule.application.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.strategy.StudentScheduleChangeStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.AwayEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AwayScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.AwayRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AwayScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

@Component
@RequiredArgsConstructor
public class AwayStudentScheduleChangeStrategy implements StudentScheduleChangeStrategy {

    private final ScheduleRepository scheduleRepository;
    private final AwayRepository awayRepository;
    private final AwayScheduleRepository awayScheduleRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.AWAY;
    }

    @Override
    public void change(StudentScheduleEntity studentSchedule, TeacherEntity teacher) {
        // 새로운 스케줄 엔티티 생성
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(studentSchedule, lastStackOrder, ScheduleType.AWAY);

        // 조퇴 엔티티 생성
        AwayEntity away = AwayEntity.createAwayEntity(studentSchedule, teacher);

        // 조퇴 스케줄 생성
        AwayScheduleEntity awayScheduleEntity = AwayScheduleEntity.builder()
                .schedule(newSchedule)
                .away(away)
                .build();

        scheduleRepository.save(newSchedule);
        awayRepository.save(away);
        awayScheduleRepository.save(awayScheduleEntity);
    }
}
