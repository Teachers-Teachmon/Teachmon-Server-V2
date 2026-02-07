package solvit.teachmon.domain.student_schedule.application.strategy.change.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.strategy.change.StudentScheduleChangeStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.ExitScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ExitRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.ExitScheduleRepository;
import solvit.teachmon.domain.student_schedule.exception.ExitScheduleNotFoundException;
import solvit.teachmon.domain.student_schedule.exception.ScheduleNotFoundException;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

@Component
@RequiredArgsConstructor
public class ExitStudentScheduleChangeStrategy implements StudentScheduleChangeStrategy {

    private final ScheduleRepository scheduleRepository;
    private final ExitRepository exitRepository;
    private final ExitScheduleRepository exitScheduleRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.EXIT;
    }

    @Override
    public void change(StudentScheduleEntity studentSchedule, TeacherEntity teacher) {
        // 기존 이탈 스케줄 삭제
        scheduleRepository.deleteByStudentScheduleIdAndType(studentSchedule.getId(), ScheduleType.EXIT);

        // 새로운 스케줄 엔티티 생성
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(studentSchedule, lastStackOrder, ScheduleType.EXIT);

        // 이탈 엔티티 생성
        ExitEntity exit = ExitEntity.createExitEntity(studentSchedule, teacher);

        // 이탈 스케줄 생성
        ExitScheduleEntity exitSchedule = ExitScheduleEntity.builder()
                .schedule(newSchedule)
                .exit(exit)
                .build();

        scheduleRepository.save(newSchedule);
        exitRepository.save(exit);
        exitScheduleRepository.save(exitSchedule);
    }

    @Override
    public void cancel(StudentScheduleEntity studentSchedule) {
        // 기존 스케줄 조회
        ScheduleEntity schedule = scheduleRepository.findByStudentScheduleIdAndType(studentSchedule.getId(), ScheduleType.EXIT)
                .orElseThrow(ScheduleNotFoundException::new);

        // 이탈 스케줄 조회
        ExitScheduleEntity exitSchedule = exitScheduleRepository.findByScheduleId(schedule.getId())
                .orElseThrow(ExitScheduleNotFoundException::new);

        // 이탈 조회
        ExitEntity exit = exitSchedule.getExit();

        // 이탈 삭제시 cascade 로 이탈 스케줄도 함께 삭제됨
        exitRepository.delete(exit);
    }
}
