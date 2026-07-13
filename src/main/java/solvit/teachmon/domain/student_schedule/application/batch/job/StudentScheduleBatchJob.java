package solvit.teachmon.domain.student_schedule.application.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AdditionalSelfStudyScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AfterSchoolReinforcementScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AfterSchoolScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.FixedLeaveSeatScheduleSettingStrategy;
import org.springframework.transaction.PlatformTransactionManager;
import solvit.teachmon.domain.student_schedule.application.batch.tasklet.StudentScheduleGeneratorTasklet;
import solvit.teachmon.domain.student_schedule.application.batch.reader.SelfStudyScheduleReader;
import solvit.teachmon.domain.student_schedule.application.batch.processor.SelfStudyScheduleProcessor;
import solvit.teachmon.domain.student_schedule.application.batch.writer.SelfStudyScheduleWriter;
import solvit.teachmon.domain.student_schedule.application.batch.dto.SelfStudyScheduleDto;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StudentScheduleBatchJob {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Qualifier("batchTaskExecutor")
    private final TaskExecutor batchTaskExecutor;

    private final StudentScheduleGeneratorTasklet generatorTasklet;
    private final SelfStudyScheduleReader selfStudyReader;
    private final SelfStudyScheduleProcessor selfStudyProcessor;
    private final SelfStudyScheduleWriter selfStudyWriter;
    private final AdditionalSelfStudyScheduleSettingStrategy additionalSelfStudyScheduleSettingStrategy;
    private final FixedLeaveSeatScheduleSettingStrategy fixedLeaveSeatScheduleSettingStrategy;
    private final AfterSchoolScheduleSettingStrategy afterSchoolScheduleSettingStrategy;
    private final AfterSchoolReinforcementScheduleSettingStrategy afterSchoolReinforcementScheduleSettingStrategy;
    private final BatchJobRestartListener batchJobRestartListener;

    @Bean
    public Job studentScheduleJob() {
        return new JobBuilder("studentScheduleJob", jobRepository)
                .listener(batchJobRestartListener)
                .start(step0GenerateEmpty())
                .next(parallelFlowStep())
                .build();
    }

    @Bean
    public Step step0GenerateEmpty() {
        return new StepBuilder("step0-generateEmpty", jobRepository)
                .tasklet(generatorTasklet, transactionManager)
                .allowStartIfComplete(false)
                .build();
    }

    @Bean
    public Flow parallelStepsFlow() {
        Flow selfStudyFlow = new FlowBuilder<SimpleFlow>("selfStudyFlow")
                .start(step1SelfStudy())
                .next(step2AdditionalSelfStudy())
                .build();

        Flow fixedLeaveSeatFlow = new FlowBuilder<SimpleFlow>("fixedLeaveSeatFlow")
                .start(step3FixedLeaveSeat())
                .build();

        Flow afterSchoolFlow = new FlowBuilder<SimpleFlow>("afterSchoolFlow")
                .start(step4AfterSchool())
                .build();

        Flow afterSchoolReinforcementFlow = new FlowBuilder<SimpleFlow>("afterSchoolReinforcementFlow")
                .start(step5AfterSchoolReinforcement())
                .build();

        return new FlowBuilder<SimpleFlow>("parallelFlow")
                .split(batchTaskExecutor)
                .add(selfStudyFlow, fixedLeaveSeatFlow, afterSchoolFlow, afterSchoolReinforcementFlow)
                .build();
    }

    @Bean
    public Step parallelFlowStep() {
        return new StepBuilder("parallel-flow-step", jobRepository)
                .flow(parallelStepsFlow())
                .build();
    }

    @Bean
    public Step step1SelfStudy() {
        return new StepBuilder("step1-selfStudy", jobRepository)
                .<SelfStudyEntity, List<SelfStudyScheduleDto>>chunk(100, transactionManager)
                .reader(selfStudyReader)
                .processor(selfStudyProcessor)
                .writer(selfStudyWriter)
                .build();
    }

    @Bean
    public Step step2AdditionalSelfStudy() {
        return new StepBuilder("step2-additionalSelfStudy", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate baseDate = (LocalDate) chunkContext.getStepContext().getJobParameters().get("baseDate");
                    ConcurrentHashMap<Long, AtomicInteger> maxStackOrderMap = getStackOrderMap(chunkContext);
                    Set<String> occupiedPlaceSet = getOccupiedPlaceSet(chunkContext);
                    additionalSelfStudyScheduleSettingStrategy.settingSchedule(baseDate, maxStackOrderMap, occupiedPlaceSet);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step3FixedLeaveSeat() {
        return new StepBuilder("step3-fixedLeaveSeat", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate baseDate = (LocalDate) chunkContext.getStepContext().getJobParameters().get("baseDate");
                    fixedLeaveSeatScheduleSettingStrategy.settingSchedule(baseDate);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step4AfterSchool() {
        return new StepBuilder("step4-afterSchool", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate baseDate = (LocalDate) chunkContext.getStepContext().getJobParameters().get("baseDate");
                    ConcurrentHashMap<Long, AtomicInteger> maxStackOrderMap = getStackOrderMap(chunkContext);
                    Set<String> businessTripSet = getBusinessTripSet(chunkContext);
                    afterSchoolScheduleSettingStrategy.settingSchedule(baseDate, maxStackOrderMap, businessTripSet);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step step5AfterSchoolReinforcement() {
        return new StepBuilder("step5-afterSchoolReinforcement", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate baseDate = (LocalDate) chunkContext.getStepContext().getJobParameters().get("baseDate");
                    ConcurrentHashMap<Long, AtomicInteger> maxStackOrderMap = getStackOrderMap(chunkContext);
                    afterSchoolReinforcementScheduleSettingStrategy.settingSchedule(baseDate, maxStackOrderMap);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<Long, AtomicInteger> getStackOrderMap(
            org.springframework.batch.core.scope.context.ChunkContext chunkContext) {
        return (ConcurrentHashMap<Long, AtomicInteger>) chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("stackOrderMap");
    }

    @SuppressWarnings("unchecked")
    private Set<String> getBusinessTripSet(
            org.springframework.batch.core.scope.context.ChunkContext chunkContext) {
        Object val = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("businessTripSet");
        return val != null ? (Set<String>) val : java.util.Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    private Set<String> getOccupiedPlaceSet(
            org.springframework.batch.core.scope.context.ChunkContext chunkContext) {
        Object val = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get("occupiedPlaceSet");
        return val != null ? (Set<String>) val : java.util.Collections.emptySet();
    }
}
