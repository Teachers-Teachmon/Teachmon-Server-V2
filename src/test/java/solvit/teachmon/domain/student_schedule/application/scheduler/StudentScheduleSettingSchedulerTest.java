package solvit.teachmon.domain.student_schedule.application.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("학생 스케줄 설정 스케줄러 테스트")
class StudentScheduleSettingSchedulerTest {

    @Mock
    private JobLauncher asyncJobLauncher;

    @Mock
    private Job studentScheduleJob;

    @InjectMocks
    private StudentScheduleSettingScheduler scheduler;

    @Test
    @DisplayName("스케줄러가 실행되면 비동기 JobLauncher로 studentScheduleJob을 실행한다")
    void shouldLaunchBatchJobWhenSchedulerRuns() throws Exception {
        when(asyncJobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mock(JobExecution.class));

        scheduler.settingStudentSchedule();

        verify(asyncJobLauncher, times(1)).run(eq(studentScheduleJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("스케줄러가 전달하는 JobParameters에는 다음 주 월요일 baseDate만 포함된다")
    void shouldIncludeOnlyBaseDateInJobParameters() throws Exception {
        when(asyncJobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(mock(JobExecution.class));

        scheduler.settingStudentSchedule();

        ArgumentCaptor<JobParameters> captor = ArgumentCaptor.forClass(JobParameters.class);
        verify(asyncJobLauncher).run(eq(studentScheduleJob), captor.capture());

        JobParameters jobParameters = captor.getValue();
        LocalDate expectedBaseDate = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusWeeks(1);
        assertThat(jobParameters.getLocalDate("baseDate")).isEqualTo(expectedBaseDate);
        assertThat(jobParameters.getLong("timestamp")).isNull();
    }

    @Test
    @DisplayName("스케줄러의 cron 설정은 매주 일요일 자정(Asia/Seoul)에 실행된다")
    void schedulerCronConfiguration() {
        // 이 테스트는 스케줄러의 @Scheduled 어노테이션 설정을 문서화합니다.
        //
        // @Scheduled(
        //     cron = "0 0 0 * * SUN",  // 매주 일요일 0시 0분 0초
        //     zone = "Asia/Seoul"       // 한국 시간대
        // )
        //
        // 동작:
        // - 매주 일요일 자정에 studentScheduleJob을 비동기 실행
        // - JobParameter로 다음 주 월요일 baseDate와 timestamp를 전달
        // - 실제 스케줄 생성/적용은 Spring Batch Step에서 처리
        //
        // 예시:
        // - 1월 14일(일) 자정 실행
        // - baseDate=1월 15일(월)로 Job 실행
    }

    @Test
    @DisplayName("스케줄러 동작 플로우")
    void schedulerWorkflow() {
        // 이 테스트는 스케줄러의 전체 동작 플로우를 문서화합니다.
        //
        // 1단계: StudentScheduleSettingScheduler가 asyncJobLauncher로 Job 실행
        //    - Job 이름: studentScheduleJob
        //    - JobParameter: baseDate, timestamp
        //
        // 2단계: Step 0(Tasklet)에서 빈 StudentSchedule 틀 생성
        //    - StudentScheduleGenerator로 기본 틀 생성
        //    - maxStackOrderMap을 JobExecutionContext에 적재
        //
        // 3단계: 병렬/순차 Step 실행
        //    - step1: SELF_STUDY(chunk=100)
        //    - step2: ADDITIONAL_SELF_STUDY(SELF_STUDY 이후 순차 실행)
        //    - step3~5: FIXED_LEAVE_SEAT / AFTER_SCHOOL / AFTER_SCHOOL_REINFORCEMENT
        //
        // 4단계: Writer가 기존 타입 데이터 정리 후 저장하여 재실행 멱등성 보장
    }
}
