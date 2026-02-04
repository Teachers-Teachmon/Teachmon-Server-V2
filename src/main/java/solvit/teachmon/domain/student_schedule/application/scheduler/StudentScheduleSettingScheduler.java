package solvit.teachmon.domain.student_schedule.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleSettingService;

@Component
@RequiredArgsConstructor
public class StudentScheduleSettingScheduler {
    private final StudentScheduleSettingService studentScheduleSettingService;

    @Scheduled(
            cron = "0 0 0 * * SUN",
            zone = "Asia/Seoul"
    )
    public void settingStudentSchedule() {
        studentScheduleSettingService.createNewStudentSchedule();
        studentScheduleSettingService.settingAllTypeSchedule();
    }
}
