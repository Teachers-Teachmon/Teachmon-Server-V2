package solvit.teachmon.domain.student_schedule.application.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 배치 재시작 시 ExecutionContext 복원 리스너
 *
 * step0가 이미 COMPLETED 상태일 때(= 재시작 케이스) Job은 step0를 스킵함.
 * 이 경우 JobExecutionContext의 maxStackOrderMap이 비어있어 병렬 Step들이 실패함.
 * 따라서 Job 시작 시점에 step0 완료 여부를 확인하고, 완료됐으면 DB에서 maxStackOrderMap을 복원함.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BatchJobRestartListener implements JobExecutionListener {

    private static final String STEP0_NAME = "step0-generateEmpty";
    private static final String MAX_STACK_ORDER_MAP_KEY = "maxStackOrderMap";

    private final ScheduleRepository scheduleRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        boolean isStep0AlreadyCompleted = jobExecution.getExecutionContext()
                .containsKey(MAX_STACK_ORDER_MAP_KEY);

        if (isStep0AlreadyCompleted) {
            log.info("[BatchJobRestartListener] 재시작 감지 - maxStackOrderMap 복원 시작");
            restoreMaxStackOrderMap(jobExecution.getExecutionContext());
            return;
        }

        // step0가 이전 JobExecution에서 완료됐는지 확인 (같은 JobInstance의 이전 실행)
        if (isPreviousStep0Completed(jobExecution)) {
            log.info("[BatchJobRestartListener] 이전 실행에서 step0 완료 확인 - maxStackOrderMap 복원 시작");
            restoreMaxStackOrderMap(jobExecution.getExecutionContext());
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("[BatchJobRestartListener] Job 실패 - baseDate={}, 실패 Step={}",
                    jobExecution.getJobParameters().getLocalDate("baseDate"),
                    getFailedStepNames(jobExecution));
        }
    }

    private boolean isPreviousStep0Completed(JobExecution jobExecution) {
        return jobExecution.getStepExecutions().stream()
                .filter(step -> STEP0_NAME.equals(step.getStepName()))
                .anyMatch(step -> step.getStatus() == BatchStatus.COMPLETED);
    }

    private void restoreMaxStackOrderMap(ExecutionContext executionContext) {
        Map<Long, Integer> maxStackOrderMap = new LinkedHashMap<>();
        scheduleRepository.findMaxStackOrderGroupByStudentScheduleId()
                .forEach(row -> maxStackOrderMap.put(row.getStudentScheduleId(), row.getMaxStackOrder()));

        executionContext.put(MAX_STACK_ORDER_MAP_KEY, maxStackOrderMap);
        log.info("[BatchJobRestartListener] maxStackOrderMap 복원 완료 - {}건", maxStackOrderMap.size());
    }

    private String getFailedStepNames(JobExecution jobExecution) {
        return jobExecution.getStepExecutions().stream()
                .filter(step -> step.getStatus() == BatchStatus.FAILED)
                .map(StepExecution::getStepName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("알 수 없음");
    }
}