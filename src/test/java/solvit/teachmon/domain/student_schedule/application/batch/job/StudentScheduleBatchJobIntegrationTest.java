package solvit.teachmon.domain.student_schedule.application.batch.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleGenerator;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AdditionalSelfStudyScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AfterSchoolReinforcementScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AfterSchoolScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.FixedLeaveSeatScheduleSettingStrategy;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("StudentScheduleBatchJob 통합 테스트")
class StudentScheduleBatchJobIntegrationTest {

    @Autowired
    @Qualifier("asyncJobLauncher")
    private JobLauncher asyncJobLauncher;

    @Autowired
    @Qualifier("studentScheduleJob")
    private Job studentScheduleJob;

    @Autowired
    private BranchRepository branchRepository;

    @SpyBean
    private StudentScheduleGenerator studentScheduleGenerator;

    @SpyBean
    private AdditionalSelfStudyScheduleSettingStrategy additionalSelfStudyScheduleSettingStrategy;

    @SpyBean
    private FixedLeaveSeatScheduleSettingStrategy fixedLeaveSeatScheduleSettingStrategy;

    @SpyBean
    private AfterSchoolScheduleSettingStrategy afterSchoolScheduleSettingStrategy;

    @SpyBean
    private AfterSchoolReinforcementScheduleSettingStrategy afterSchoolReinforcementScheduleSettingStrategy;

    @BeforeEach
    void setUpBranch() {
        LocalDate startDay = LocalDate.of(2026, 4, 1);
        LocalDate endDay = LocalDate.of(2026, 6, 30);

        branchRepository.deleteAll();
        branchRepository.save(
                BranchEntity.builder()
                        .startDay(startDay)
                        .endDay(endDay)
                        .afterSchoolEndDay(endDay.minusDays(7))
                        .year(2026)
                        .branch(2)
                        .build()
        );
    }

    @Test
    @DisplayName("배치 실행 시 step들이 완료되고 각 전략 step이 호출된다")
    void shouldCompleteBatchJobAndCallStrategies() throws Exception {
        LocalDate baseDate = LocalDate.of(2026, 4, 6);
        JobExecution execution = asyncJobLauncher.run(
                studentScheduleJob,
                new JobParametersBuilder()
                        .addLocalDate("baseDate", baseDate)
                        .addLong("timestamp", System.currentTimeMillis())
                        .toJobParameters()
        );

        waitForCompletion(execution);

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        verify(studentScheduleGenerator, atLeastOnce()).deleteFutureStudentSchedules(baseDate);
        verify(studentScheduleGenerator, atLeastOnce()).createStudentScheduleByStudents(org.mockito.ArgumentMatchers.anyList(), org.mockito.ArgumentMatchers.eq(baseDate));
        verify(additionalSelfStudyScheduleSettingStrategy, atLeastOnce()).settingSchedule(org.mockito.ArgumentMatchers.eq(baseDate), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(fixedLeaveSeatScheduleSettingStrategy, atLeastOnce()).settingSchedule(baseDate);
        verify(afterSchoolScheduleSettingStrategy, atLeastOnce()).settingSchedule(org.mockito.ArgumentMatchers.eq(baseDate), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(afterSchoolReinforcementScheduleSettingStrategy, atLeastOnce()).settingSchedule(org.mockito.ArgumentMatchers.eq(baseDate), org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("동일 baseDate로 재실행해도 배치가 완료된다")
    void shouldBeRerunnableWithDifferentTimestamp() throws Exception {
        LocalDate baseDate = LocalDate.of(2026, 4, 6);

        JobExecution first = asyncJobLauncher.run(
                studentScheduleJob,
                new JobParametersBuilder()
                        .addLocalDate("baseDate", baseDate)
                        .addLong("timestamp", System.currentTimeMillis())
                        .toJobParameters()
        );
        waitForCompletion(first);

        JobExecution second = asyncJobLauncher.run(
                studentScheduleJob,
                new JobParametersBuilder()
                        .addLocalDate("baseDate", baseDate)
                        .addLong("timestamp", System.currentTimeMillis() + 1)
                        .toJobParameters()
        );
        waitForCompletion(second);

        assertThat(first.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(second.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    private void waitForCompletion(JobExecution execution) throws InterruptedException {
        int remain = 200;
        while (execution.isRunning() && remain-- > 0) {
            Thread.sleep(50L);
        }
    }
}
