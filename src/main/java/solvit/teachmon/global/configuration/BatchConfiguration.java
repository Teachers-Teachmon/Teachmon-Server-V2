package solvit.teachmon.global.configuration;

import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 이중 Executor 배치 구조
 * - Job 레벨: SimpleAsyncTaskExecutor (요청 시점에만 스레드 생성/소멸)
 * - Step 레벨: ThreadPoolTaskExecutor (병렬 실행용 풀 관리)
 */
@Configuration
public class BatchConfiguration {

    /**
     * Job 레벨 비동기 실행 - SimpleAsyncTaskExecutor
     * Job이 1개이므로 스레드 풀 관리 오버헤드 없이 요청 시점에만 스레드 생성/소멸
     */
    @Bean
    public JobLauncher asyncJobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    /**
     * Step 레벨 병렬 실행 - ThreadPoolTaskExecutor
     * FlowBuilder.split()에서 4개 Step을 병렬 실행하기 위한 스레드 풀
     * corePoolSize=4, maxPoolSize=4로 고정하여 정확히 4개 Step이 동시 실행되도록 보장
     */
    @Bean(name = "batchTaskExecutor")
    public TaskExecutor batchTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("batch-step-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
