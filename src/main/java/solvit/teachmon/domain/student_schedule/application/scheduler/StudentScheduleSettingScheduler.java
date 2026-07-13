package solvit.teachmon.domain.student_schedule.application.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * 매주 일요일 00시에 JobLauncher를 트리거하여 Student Schedule Batch Job 실행
 * JobLauncher는 SimpleAsyncTaskExecutor를 통해 Job을 비동기 스레드로 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StudentScheduleSettingScheduler {
    @Qualifier("asyncJobLauncher")
    private final JobLauncher asyncJobLauncher;
    
    private final Job studentScheduleJob;

    @Scheduled(
            cron = "0 0 0 * * SUN",
            zone = "Asia/Seoul"
    )
    public void settingStudentSchedule() throws Exception {
        LocalDate baseDate = LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(1);
        
        log.info("Triggering Student Schedule Batch Job for baseDate={}", baseDate);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDate("baseDate", baseDate)
                .toJobParameters();

        asyncJobLauncher.run(studentScheduleJob, jobParameters);
        
        log.info("Student Schedule Batch Job launched asynchronously");
    }
}
