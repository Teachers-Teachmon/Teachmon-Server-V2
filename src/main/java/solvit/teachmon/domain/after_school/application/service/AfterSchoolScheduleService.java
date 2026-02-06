package solvit.teachmon.domain.after_school.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.vo.StudentAssignmentResultVo;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AfterSchoolScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AfterSchoolScheduleRepository;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AfterSchoolScheduleService {
    private final AfterSchoolScheduleRepository afterSchoolScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;

    public void save(List<StudentAssignmentResultVo> studentAssignmentResultVo) {
        if(!checkUpdatableSchedule()) return;
        studentAssignmentResultVo.forEach((assignmentResultVo) -> {
            AfterSchoolEntity afterSchool = assignmentResultVo.afterSchool();
            LocalDateTime now = currentDateTime();
            LocalDate afterSchoolDate =
                    now.toLocalDate().with(TemporalAdjusters.previousOrSame(afterSchool.getWeekDay().toDayOfWeek()));

            LocalDateTime afterSchoolEnd = afterSchoolDate.atTime(afterSchool.getPeriod().getEndTime());

            if (now.isAfter(afterSchoolEnd)) {
                return;
            }

            List<StudentScheduleEntity> studentScheduleEntities = studentScheduleRepository.findAllByStudentsAndDayAndPeriod(
                    assignmentResultVo.addedStudents(),
                    afterSchoolDate,
                    afterSchool.getPeriod()
            );
            List<ScheduleEntity> studentSchedules = saveSchedule(studentScheduleEntities);
            for (ScheduleEntity scheduleEntity : studentSchedules) {
                createAfterSchoolSchedule(scheduleEntity, afterSchool);
            }
        });
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

    private List<ScheduleEntity> saveSchedule(List<StudentScheduleEntity> studentScheduleEntities) {
        return studentScheduleEntities.stream().map((studentScheduleEntity) -> {
            Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentScheduleEntity.getId());
            ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(studentScheduleEntity, lastStackOrder, ScheduleType.AFTER_SCHOOL);
            scheduleRepository.save(newSchedule);
            return newSchedule;
        }).toList();
    }

    private boolean checkUpdatableSchedule() {
        LocalDateTime now = currentDateTime();

        LocalDateTime start = now
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                .with(LocalTime.MIN);

        LocalDateTime end = now
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY))
                .with(LocalTime.of(20, 40, 0));

        return now.isAfter(start) && now.isBefore(end);
    }

    protected LocalDateTime currentDateTime() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
