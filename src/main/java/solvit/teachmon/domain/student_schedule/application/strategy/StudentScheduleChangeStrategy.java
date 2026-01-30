package solvit.teachmon.domain.student_schedule.application.strategy;

import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

public interface StudentScheduleChangeStrategy {
    ScheduleType getScheduleType();
    void change(StudentScheduleEntity studentSchedule, TeacherEntity teacher);
    void cancel(StudentScheduleEntity studentSchedule);
}