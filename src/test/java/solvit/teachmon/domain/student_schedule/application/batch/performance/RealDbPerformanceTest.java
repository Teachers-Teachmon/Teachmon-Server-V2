package solvit.teachmon.domain.student_schedule.application.batch.performance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleGenerator;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AdditionalSelfStudyScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AfterSchoolReinforcementScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.AfterSchoolScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.FixedLeaveSeatScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.impl.SelfStudyScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockingDetails;

/**
 * 실제 MySQL DB (teachmon_dump.sql 로드 상태) 기반 V2 vs V3 성능 비교
 *
 * 실행 조건:
 *   docker run -d --name teachmon-perf-mysql -e MYSQL_ROOT_PASSWORD=root
 *              -e MYSQL_DATABASE=teachmon -p 3307:3306 mysql:8.0
 *   teachmon_dump.sql 로드 완료 상태
 *
 * baseDate = 2026-04-27 (dump 데이터의 branch 1기: 2026-03-09 ~ 2026-04-30 포함)
 *   - 학생 183명
 *   - self_study 26건
 *   - after_school 51건 (미종료)
 */
@SpringBootTest
@ActiveProfiles("perf")
@DisplayName("[실DB] V2 vs V3 실제 스케줄 세팅 성능 비교")
class RealDbPerformanceTest {

    private static final LocalDate BASE_DATE = LocalDate.of(2026, 4, 27);

    @Autowired
    private SelfStudyScheduleSettingStrategy selfStudyScheduleSettingStrategy;

    @Autowired
    private AdditionalSelfStudyScheduleSettingStrategy additionalSelfStudyScheduleSettingStrategy;

    @Autowired
    private FixedLeaveSeatScheduleSettingStrategy fixedLeaveSeatScheduleSettingStrategy;

    @Autowired
    private AfterSchoolScheduleSettingStrategy afterSchoolScheduleSettingStrategy;

    @Autowired
    private AfterSchoolReinforcementScheduleSettingStrategy afterSchoolReinforcementScheduleSettingStrategy;

    @Autowired
    private StudentScheduleGenerator studentScheduleGenerator;

    @Autowired
    @Qualifier("asyncJobLauncher")
    private JobLauncher asyncJobLauncher;

    @Autowired
    @Qualifier("studentScheduleJob")
    private Job studentScheduleJob;

    @Autowired
    private StudentRepository studentRepository;

    @SpyBean
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudentScheduleRepository studentScheduleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private solvit.teachmon.domain.student_schedule.application.batch.stepscope.StudentScheduleInfoMapHolder studentScheduleInfoMapHolder;

    @BeforeEach
    void cleanSchedules() {
        deleteAllSchedules();
        Mockito.clearInvocations(scheduleRepository);
    }

    @AfterEach
    void restore() {
        deleteAllSchedules();
    }

    /** FK 제약 순서대로 자식 → 부모 삭제 */
    private void deleteAllSchedules() {
        jdbcTemplate.execute("DELETE FROM additional_self_study_schedule");
        jdbcTemplate.execute("DELETE FROM after_school_schedule");
        jdbcTemplate.execute("DELETE FROM leave_seat_schedule");
        jdbcTemplate.execute("DELETE FROM self_study_schedule");
        jdbcTemplate.execute("DELETE FROM exit_schedule");
        jdbcTemplate.execute("DELETE FROM away_schedule");
        scheduleRepository.deleteAllInBatch();
        studentScheduleRepository.deleteAllInBatch();
        // FixedLeaveSeat 전략이 생성하는 leave_seat도 초기화 (V2↔V3 비교 시 상태 동기화)
        jdbcTemplate.execute("DELETE FROM leave_seat_student");
        jdbcTemplate.execute("DELETE FROM leave_seat");
    }

    @Test
    @DisplayName("[V3 단독] 쿼리 패턴 분석")
    void v3_only_query_analysis() throws Exception {
        JobExecution execution = asyncJobLauncher.run(
                studentScheduleJob,
                new JobParametersBuilder()
                        .addLocalDate("baseDate", BASE_DATE)
                        .addLong("timestamp", System.currentTimeMillis())
                        .toJobParameters()
        );
        waitForCompletion(execution);
        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        System.out.println("[V3 단독] schedule count=" + scheduleRepository.count());
    }

    @Test
    @DisplayName("[최종] V2 vs V3 실제 DB stackOrder 쿼리 횟수 및 성능 비교")
    void compare_v2_vs_v3_realDb() throws Exception {
        int studentCount = studentRepository.findByYear(2026).size();

        // ── V3 측정 ──
        Mockito.clearInvocations(scheduleRepository);
        long v3Start = System.currentTimeMillis();

        JobExecution execution = asyncJobLauncher.run(
                studentScheduleJob,
                new JobParametersBuilder()
                        .addLocalDate("baseDate", BASE_DATE)
                        .addLong("timestamp", System.currentTimeMillis())
                        .toJobParameters()
        );
        waitForCompletion(execution);

        long v3Elapsed = System.currentTimeMillis() - v3Start;
        long v3StackOrderCalls = mockingDetails(scheduleRepository).getInvocations().stream()
                .filter(inv -> inv.getMethod().getName().equals("findLastStackOrderByStudentScheduleId"))
                .count();
        long v3ScheduleCount = scheduleRepository.count();

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // ── V2 측정 ──
        deleteAllSchedules();
        studentScheduleInfoMapHolder.clear();

        var students = studentRepository.findByYear(2026);
        studentScheduleGenerator.createStudentScheduleByStudents(students, BASE_DATE);

        Mockito.clearInvocations(scheduleRepository);
        long v2Start = System.currentTimeMillis();

        // 배치 V3와 동일한 5개 전략만 순차 실행 (LEAVE_SEAT, EXIT, AWAY 제외)
        // 전략들이 @Transactional 없이 save()를 여러 번 호출하므로 트랜잭션으로 감싸야 detach 방지
        transactionTemplate.executeWithoutResult(status -> {
            selfStudyScheduleSettingStrategy.settingSchedule(BASE_DATE);
            additionalSelfStudyScheduleSettingStrategy.settingSchedule(BASE_DATE);
            fixedLeaveSeatScheduleSettingStrategy.settingSchedule(BASE_DATE);
            afterSchoolScheduleSettingStrategy.settingSchedule(BASE_DATE);
            afterSchoolReinforcementScheduleSettingStrategy.settingSchedule(BASE_DATE);
        });

        long v2Elapsed = System.currentTimeMillis() - v2Start;
        long v2StackOrderCalls = mockingDetails(scheduleRepository).getInvocations().stream()
                .filter(inv -> inv.getMethod().getName().equals("findLastStackOrderByStudentScheduleId"))
                .count();
        long v2ScheduleCount = scheduleRepository.count();

        // ── 결과 출력 ──
        long savedCalls = v2StackOrderCalls - v3StackOrderCalls;
        double reductionRate = v2StackOrderCalls > 0
                ? (double) savedCalls / v2StackOrderCalls * 100 : 0;
        double speedup = v2Elapsed > 0 ? (double) v2Elapsed / v3Elapsed : 0;

        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║    [실제 MySQL] V2 vs V3 스케줄 세팅 성능 비교 결과                  ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  학생 수                         : %3d명%n", studentCount);
        System.out.printf("║  baseDate                        : %s%n", BASE_DATE);
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  [V2] findLastStackOrder 호출 수 : %5d회  (각 전략 개별 조회)%n", v2StackOrderCalls);
        System.out.printf("║  [V2] 소요 시간                  : %5d ms%n", v2Elapsed);
        System.out.printf("║  [V2] 생성 Schedule 수           : %5d건%n", v2ScheduleCount);
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  [V3] findLastStackOrder 호출 수 : %5d회  (Step0 집계 1회 = 0호출)%n", v3StackOrderCalls);
        System.out.printf("║  [V3] 소요 시간                  : %5d ms%n", v3Elapsed);
        System.out.printf("║  [V3] 생성 Schedule 수           : %5d건%n", v3ScheduleCount);
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  절감 호출 수                    : %5d회  (%.1f%% 감소)%n", savedCalls, reductionRate);
        System.out.printf("║  V3 속도                         :   %.2fx 빠름%n", speedup);
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");

        assertThat(v3StackOrderCalls).isLessThan(v2StackOrderCalls);
        assertThat(v3ScheduleCount).isGreaterThan(0);
    }

    private void waitForCompletion(JobExecution execution) throws InterruptedException {
        int remain = 1200; // 최대 60초 대기
        while (execution.isRunning() && remain-- > 0) {
            Thread.sleep(50L);
        }
    }
}
