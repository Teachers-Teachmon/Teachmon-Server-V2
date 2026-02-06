package solvit.teachmon.domain.student_schedule.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleSettingService;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class StudentScheduleSettingScheduler {
    private final StudentScheduleSettingService studentScheduleSettingService;

    @Scheduled(
            cron = "0 0 0 * * SUN",
            zone = "Asia/Seoul"
    )
    public void settingStudentSchedule() {
        LocalDate baseDate = LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(1);
        studentScheduleSettingService.createNewStudentSchedule(baseDate);
        studentScheduleSettingService.settingAllTypeSchedule(baseDate);
    }
}
