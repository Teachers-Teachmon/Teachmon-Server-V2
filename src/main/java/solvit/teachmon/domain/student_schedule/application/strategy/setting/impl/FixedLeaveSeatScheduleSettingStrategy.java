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
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FixedLeaveSeatScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final LeaveSeatRepository leaveSeatRepository;
    private final FixedLeaveSeatRepository fixedLeaveSeatRepository;
    private final FixedLeaveSeatStudentRepository fixedLeaveSeatStudentRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.FIXED_LEAVE_SEAT;
    }

    @Override
    public void settingSchedule(LocalDate baseDate) {
        List<FixedLeaveSeatEntity> fixedLeaveSeats = fixedLeaveSeatRepository.findAll();

        for (FixedLeaveSeatEntity fixedLeaveSeat : fixedLeaveSeats) {
            LocalDate leaveSeatDay = calculateFixedLeaveSeatDay(fixedLeaveSeat, baseDate);

            if (isBeforeLeaveSeat(fixedLeaveSeat, baseDate))
                continue;

            if (isAlreadyCreated(fixedLeaveSeat, leaveSeatDay))
                continue;

            createLeaveSeat(fixedLeaveSeat, leaveSeatDay);
        }
    }

    private Boolean isBeforeLeaveSeat(FixedLeaveSeatEntity fixedLeaveSeat, LocalDate baseDate) {
        LocalDate fixedLeaveSeatDay = calculateFixedLeaveSeatDay(fixedLeaveSeat, baseDate);
        return fixedLeaveSeatDay.isBefore(baseDate);
    }

    private LocalDate calculateFixedLeaveSeatDay(FixedLeaveSeatEntity fixedLeaveSeat, LocalDate baseDate) {
        return baseDate.with(fixedLeaveSeat.getWeekDay().toDayOfWeek());
    }

    private boolean isAlreadyCreated(FixedLeaveSeatEntity fixedLeaveSeat, LocalDate leaveSeatDay) {
        return leaveSeatRepository
                .findByPlaceAndDayAndPeriod(fixedLeaveSeat.getPlace(), leaveSeatDay, fixedLeaveSeat.getPeriod())
                .isPresent();
    }

    private void createLeaveSeat(FixedLeaveSeatEntity fixedLeaveSeat, LocalDate leaveSeatDay) {
        LeaveSeatEntity leaveSeat = LeaveSeatEntity.from(fixedLeaveSeat, leaveSeatDay);

        List<StudentEntity> students = fixedLeaveSeatStudentRepository.findAllByFixedLeaveSeat(fixedLeaveSeat);
        students.forEach(student -> {
            LeaveSeatStudentEntity leaveSeatStudent = LeaveSeatStudentEntity.builder()
                    .leaveSeat(leaveSeat)
                    .student(student)
                    .build();
            leaveSeat.addLeaveSeatStudent(leaveSeatStudent);
        });

        leaveSeatRepository.save(leaveSeat);
    }
}
