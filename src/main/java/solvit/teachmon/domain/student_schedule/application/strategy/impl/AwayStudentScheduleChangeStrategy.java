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
import solvit.teachmon.domain.student_schedule.exception.AwayScheduleNotFoundException;
import solvit.teachmon.domain.student_schedule.exception.ScheduleNotFoundException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.Optional;

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
        // 기존 조퇴 스케줄 삭제
        scheduleRepository.deleteByStudentScheduleIdAndType(studentSchedule.getId(), ScheduleType.AWAY);

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

    @Override
    public void cancel(StudentScheduleEntity studentSchedule) {
        // 기존 스케줄 조회
        ScheduleEntity schedule = scheduleRepository.findByStudentScheduleIdAndType(studentSchedule.getId(), ScheduleType.AWAY)
                .orElseThrow(ScheduleNotFoundException::new);

        // 조퇴 스케줄 조회
        AwayScheduleEntity awaySchedule = awayScheduleRepository.findByScheduleId(schedule.getId())
                .orElseThrow(AwayScheduleNotFoundException::new);

        // 조퇴 조회
        AwayEntity away = awaySchedule.getAway();

        // 조퇴 삭제시 cascade 로 조퇴 스케줄도 함께 삭제됨
        awayRepository.delete(away);
        scheduleRepository.delete(schedule);
    }
}
