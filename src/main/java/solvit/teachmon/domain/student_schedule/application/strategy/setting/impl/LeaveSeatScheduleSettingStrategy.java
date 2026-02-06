package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatStudentEntity;
import solvit.teachmon.domain.leave_seat.domain.repository.FixedLeaveSeatRepository;
import solvit.teachmon.domain.leave_seat.domain.repository.FixedLeaveSeatStudentRepository;
import solvit.teachmon.domain.leave_seat.domain.repository.LeaveSeatRepository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.LeaveSeatScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.LeaveSeatScheduleRepository;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveSeatScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final LeaveSeatRepository leaveSeatRepository;
    private final LeaveSeatScheduleRepository leaveSeatScheduleRepository;
    private final FixedLeaveSeatRepository fixedLeaveSeatRepository;
    private final FixedLeaveSeatStudentRepository fixedLeaveSeatStudentRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.LEAVE_SEAT;
    }

    @Override
    public void settingSchedule(LocalDate baseDate) {
        List<FixedLeaveSeatEntity> fixedLeaveSeats = fixedLeaveSeatRepository.findAll();

        for(FixedLeaveSeatEntity fixedLeaveSeat : fixedLeaveSeats) {
            List<StudentScheduleEntity> studentSchedules = findStudentScheduleByFixedLeaveSeat(fixedLeaveSeat, baseDate);
            settingLeaveSeatSchedule(studentSchedules, fixedLeaveSeat, baseDate);
        }
    }

    private List<StudentScheduleEntity> findStudentScheduleByFixedLeaveSeat(FixedLeaveSeatEntity fixedLeaveSeat, LocalDate baseDate) {
        return studentScheduleRepository.findAllByFixedLeaveSeatAndDay(
                fixedLeaveSeat,
                calculateFixedLeaveSeatDay(fixedLeaveSeat, baseDate),
                fixedLeaveSeat.getPeriod()
        );
    }

    private LocalDate calculateFixedLeaveSeatDay(FixedLeaveSeatEntity fixedLeaveSeat, LocalDate baseDate) {
        return baseDate.with(fixedLeaveSeat.getWeekDay().toDayOfWeek());
    }

    private void settingLeaveSeatSchedule(
            List<StudentScheduleEntity> studentSchedules,
            FixedLeaveSeatEntity fixedLeaveSeat,
            LocalDate baseDate
    ) {
        LeaveSeatEntity leaveSeat = createLeaveSeat(fixedLeaveSeat, baseDate);

        for(StudentScheduleEntity studentSchedule : studentSchedules) {
            ScheduleEntity newSchedule = createNewSchedule(studentSchedule);
            createLeaveSeatSchedule(newSchedule, leaveSeat);
        }
    }

    private ScheduleEntity createNewSchedule(StudentScheduleEntity studentSchedule) {
        // 새로운 스케줄 생성
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(studentSchedule, lastStackOrder, ScheduleType.LEAVE_SEAT);

        scheduleRepository.save(newSchedule);

        return newSchedule;
    }

    private LeaveSeatEntity createLeaveSeat(FixedLeaveSeatEntity fixedLeaveSeat, LocalDate baseDate) {
        // 이석 생성
        LeaveSeatEntity leaveSeat = LeaveSeatEntity.from(fixedLeaveSeat, calculateFixedLeaveSeatDay(fixedLeaveSeat, baseDate));

        // 이석 학생 추가
        List<StudentEntity> leaveSeatStudents = fixedLeaveSeatStudentRepository.findAllByFixedLeaveSeat(fixedLeaveSeat);
        leaveSeatStudents
                .forEach(student -> {
                    LeaveSeatStudentEntity leaveSeatStudent = LeaveSeatStudentEntity.builder()
                            .leaveSeat(leaveSeat)
                            .student(student)
                            .build();

                    leaveSeat.addLeaveSeatStudent(leaveSeatStudent);
                });

        leaveSeatRepository.save(leaveSeat);
        return leaveSeat;
    }

    private void createLeaveSeatSchedule(ScheduleEntity schedule, LeaveSeatEntity leaveSeat) {
        // 이석 스케줄 생성
        LeaveSeatScheduleEntity leaveSeatSchedule = LeaveSeatScheduleEntity.builder()
                .schedule(schedule)
                .leaveSeat(leaveSeat)
                .build();

        leaveSeatScheduleRepository.save(leaveSeatSchedule);
    }
}
