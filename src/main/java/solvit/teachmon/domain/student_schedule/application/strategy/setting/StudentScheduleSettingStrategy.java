package solvit.teachmon.domain.student_schedule.application.strategy.setting;

import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

import java.time.LocalDate;

public interface StudentScheduleSettingStrategy {
    ScheduleType getScheduleType();
    void settingSchedule(LocalDate baseDate);
}
