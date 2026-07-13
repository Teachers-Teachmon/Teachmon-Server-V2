package solvit.teachmon.domain.student_schedule.application.batch.performance;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.stat.Statistics;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolStudentEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolStudentRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.SelfStudyRepository;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleGenerator;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleSettingService;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * V2 vs V3 실제 DB 쿼리 횟수 비교 (Hibernate Statistics 기반)
 *
 * V2: StudentScheduleSettingService.settingAllTypeSchedule()
 *     → 각 전략이 studentSchedule마다 findLastStackOrderByStudentScheduleId() 개별 쿼리
 *
 * V3: 실제 배치 잡 (studentScheduleJob)
 *     → Step0 집계 1회 → Step1~5 Map 참조, stackOrder 개별 쿼리 없음
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("V2 vs V3 실제 DB 쿼리 횟수 비교")
class StackOrderQueryPerformanceTest {

    @Autowired
    private StudentScheduleSettingService studentScheduleSettingService;

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

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private SelfStudyRepository selfStudyRepository;

    @Autowired
    private AfterSchoolRepository afterSchoolRepository;

    @Autowired
    private AfterSchoolStudentRepository afterSchoolStudentRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private StudentScheduleRepository studentScheduleRepository;

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final int STUDENT_COUNT = 30;
    private static final LocalDate BASE_DATE = LocalDate.of(2026, 4, 6);

    private BranchEntity branch;
    private TeacherEntity teacher;
    private PlaceEntity place;

    @BeforeEach
    void setUp() {
        scheduleRepository.deleteAll();
        studentScheduleRepository.deleteAll();
        afterSchoolStudentRepository.deleteAll();
        afterSchoolRepository.deleteAll();
        selfStudyRepository.deleteAll();
        studentRepository.deleteAll();
        placeRepository.deleteAll();
        branchRepository.deleteAll();
        teacherRepository.deleteAll();

        branch = branchRepository.save(BranchEntity.builder()
                .startDay(LocalDate.of(2026, 4, 1))
                .endDay(LocalDate.of(2026, 6, 30))
                .afterSchoolEndDay(LocalDate.of(2026, 6, 23))
                .year(2026)
                .branch(2)
                .build());

        teacher = teacherRepository.save(TeacherEntity.builder()
                .mail("teacher@test.com")
                .name("테스트선생님")
                .providerId("provider-test")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build());

        // PlaceEntity는 protected 생성자라 JdbcTemplate으로 직접 insert
        // findAllByGradePrefix(1) 은 name LIKE '1-%' 로 조회하므로 반별 4개 생성
        long baseId = System.currentTimeMillis();
        for (int cls = 1; cls <= 4; cls++) {
            jdbcTemplate.update(
                    "INSERT INTO place (id, floor, name, created_at, updated_at) VALUES (?, 3, ?, NOW(), NOW())",
                    baseId + cls, "1-" + cls);
        }
        place = placeRepository.findById(baseId + 1).orElseThrow();

        for (int i = 0; i < STUDENT_COUNT; i++) {
            studentRepository.save(StudentEntity.builder()
                    .name("학생" + (i + 1))
                    .grade(1)
                    .classNumber(i % 4 + 1)
                    .number(i + 1)
                    .year(2026)
                    .build());
        }

        selfStudyRepository.save(SelfStudyEntity.builder()
                .branch(branch)
                .weekDay(WeekDay.MON)
                .period(SchoolPeriod.SEVEN_PERIOD)
                .grade(1)
                .build());

        AfterSchoolEntity afterSchool = afterSchoolRepository.save(AfterSchoolEntity.builder()
                .teacher(teacher)
                .branch(branch)
                .place(place)
                .weekDay(WeekDay.TUE)
                .period(SchoolPeriod.EIGHT_AND_NINE_PERIOD)
                .year(2026)
                .name("수학 방과후")
                .grade(1)
                .build());

        List<StudentEntity> students = studentRepository.findByYear(2026);
        students.forEach(student ->
                afterSchoolStudentRepository.save(AfterSchoolStudentEntity.builder()
                        .afterSchool(afterSchool)
                        .student(student)
                        .build()));
    }

    @Test
    @DisplayName("V2 vs V3 실제 DB 쿼리 횟수 비교 (Hibernate Statistics)")
    void compareV2vsV3_queryCount() throws Exception {
        Statistics stats = emf.unwrap(org.hibernate.SessionFactory.class).getStatistics();

        // ── V3 먼저 (Spring Batch 초기화 이미 완료 상태) ──
        stats.clear();
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
        long v3QueryCount = stats.getQueryExecutionCount();
        long v3ScheduleCount = scheduleRepository.count();

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // ── V2 측정 ──
        scheduleRepository.deleteAll();
        studentScheduleRepository.deleteAll();

        List<StudentEntity> students = studentRepository.findByYear(2026);
        studentScheduleGenerator.deleteFutureStudentSchedules(BASE_DATE);
        studentScheduleGenerator.createStudentScheduleByStudents(students, BASE_DATE);
        scheduleRepository.deleteAll();

        stats.clear();
        long v2Start = System.currentTimeMillis();

        studentScheduleSettingService.settingAllTypeSchedule(BASE_DATE);

        long v2Elapsed = System.currentTimeMillis() - v2Start;
        long v2QueryCount = stats.getQueryExecutionCount();
        long v2ScheduleCount = scheduleRepository.count();

        // ── 결과 출력 ──
        long savedQueries = v2QueryCount - v3QueryCount;

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║      V2 vs V3 실제 DB 쿼리 횟수 비교 (Hibernate Statistics)   ║");
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ 학생 수                      : %3d명%n", STUDENT_COUNT);
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ [V2] 총 쿼리 수              : %4d회%n", v2QueryCount);
        System.out.printf("║ [V2] 소요 시간               : %4d ms%n", v2Elapsed);
        System.out.printf("║ [V2] 생성된 Schedule         : %4d건%n", v2ScheduleCount);
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ [V3] 총 쿼리 수              : %4d회%n", v3QueryCount);
        System.out.printf("║ [V3] 소요 시간               : %4d ms%n", v3Elapsed);
        System.out.printf("║ [V3] 생성된 Schedule         : %4d건%n", v3ScheduleCount);
        System.out.println("╠══════════════════════════════════════════════════════════════╣");
        System.out.printf("║ 절감 쿼리 수                 : %4d회%n", savedQueries);
        System.out.printf("║ 쿼리 감소율                  : %.1f%%%n",
                v2QueryCount > 0 ? (double) savedQueries / v2QueryCount * 100 : 0);
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("[참고] V3 소요 시간에는 Spring Batch 프레임워크 오버헤드(JobRepository 저장,");
        System.out.println("       병렬 스레드 관리 등)가 포함되어 H2 in-memory에서는 ms 차이가 작음.");
        System.out.println("       실제 운영 DB에서는 쿼리 절감 효과가 네트워크 왕복 비용으로 직결됨.");

        assertThat(v3QueryCount).isLessThan(v2QueryCount);
    }

    private void waitForCompletion(JobExecution execution) throws InterruptedException {
        int remain = 200;
        while (execution.isRunning() && remain-- > 0) {
            Thread.sleep(50L);
        }
    }
}
