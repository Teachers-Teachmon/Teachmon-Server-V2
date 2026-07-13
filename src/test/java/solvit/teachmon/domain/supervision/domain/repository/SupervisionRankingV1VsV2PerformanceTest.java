package solvit.teachmon.domain.supervision.domain.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import solvit.teachmon.domain.supervision.domain.entity.QSupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionSortOrder;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionRankResponseDto;
import solvit.teachmon.domain.user.domain.entity.QTeacherEntity;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 감독 순위 조회 V1 vs V2 성능 비교 테스트
 *
 * 실DB (teachmon_dump.sql) 기반 — 테스트 실행 전에 dump가 로드되어 있어야 함
 *
 * V1 (旧): 동일 테이블 LEFT JOIN 3회 → Cartesian Product O(N³)
 *   SELECT t.id, t.name,
 *     COUNT(DISTINCT ss1.day) AS selfStudy,
 *     COUNT(DISTINCT ss2.day) AS leaveSeat,
 *     COUNT(DISTINCT ss3.day) AS seventh
 *   FROM teacher t
 *   LEFT JOIN supervision_schedule ss1 ON t.id = ss1.teacher_id AND ss1.type = 'SELF_STUDY_SUPERVISION'
 *   LEFT JOIN supervision_schedule ss2 ON t.id = ss2.teacher_id AND ss2.type = 'LEAVE_SEAT_SUPERVISION'
 *   LEFT JOIN supervision_schedule ss3 ON t.id = ss3.teacher_id AND ss3.type = 'SEVENTH_PERIOD_SUPERVISION'
 *   GROUP BY t.id
 *
 * V2 (新): 단일 LEFT JOIN + CASE WHEN 조건부 집계 → O(N)
 *   SELECT t.name,
 *     COUNT(DISTINCT CASE WHEN s.type = 'SELF_STUDY_SUPERVISION' THEN s.day END),
 *     COUNT(DISTINCT CASE WHEN s.type = 'LEAVE_SEAT_SUPERVISION' THEN s.day END),
 *     COUNT(DISTINCT CASE WHEN s.type = 'SEVENTH_PERIOD_SUPERVISION' THEN s.day END)
 *   FROM teacher t
 *   LEFT JOIN supervision_schedule s ON t.id = s.teacher_id
 *   GROUP BY t.id, t.name
 */
@SpringBootTest
@ActiveProfiles("perf")
@DisplayName("[실DB] 감독 순위 조회 V1 vs V2 성능 비교")
class SupervisionRankingV1VsV2PerformanceTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private SupervisionScheduleQueryDslRepositoryImpl supervisionQueryRepository;

    @Test
    @DisplayName("V1(3 LEFT JOINs) vs V2(단일 JOIN + CASE WHEN) 성능 비교")
    void compareV1_vs_V2() {
        // ── V1 측정: 3 LEFT JOINs (Cartesian Product) ──
        long v1Start = System.currentTimeMillis();
        List<Tuple> v1Results = executeV1Query();
        long v1Elapsed = System.currentTimeMillis() - v1Start;

        // ── V2 측정: 단일 LEFT JOIN + CASE WHEN ──
        long v2Start = System.currentTimeMillis();
        List<SupervisionRankResponseDto> v2Results = supervisionQueryRepository.findSupervisionRankings(null, SupervisionSortOrder.DESC);
        long v2Elapsed = System.currentTimeMillis() - v2Start;

        // ── 결과 검증: 건수 동일 ──
        assertThat(v2Results).hasSize(v1Results.size());

        // ── 결과 출력 ──
        double speedup = v1Elapsed > 0 ? (double) v1Elapsed / v2Elapsed : 0;
        int teacherCount = v1Results.size();

        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║         감독 순위 조회 V1 vs V2 성능 비교                         ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  선생님 수 (dump 데이터)        : %5d명%n", teacherCount);
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  [V1] 3 LEFT JOINs (Cartesian) : %6d ms%n", v1Elapsed);
        System.out.printf("║  [V2] 단일 JOIN + CASE WHEN     : %6d ms%n", v2Elapsed);
        System.out.println("╠════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║  V2 속도                         :   %.2fx %s%n", speedup, speedup > 1 ? "빠름" : "느림");
        System.out.printf("║  시간 차이                       : %+7d ms%n", v1Elapsed - v2Elapsed);
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");

        // V2는 V1보다 빨라야 함 (동일 결과的前提下)
        assertThat(v2Elapsed).isLessThanOrEqualTo(v1Elapsed);
    }

    private List<Tuple> executeV1Query() {
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;
        QSupervisionScheduleEntity ss1 = new QSupervisionScheduleEntity("ss1");
        QSupervisionScheduleEntity ss2 = new QSupervisionScheduleEntity("ss2");
        QSupervisionScheduleEntity ss3 = new QSupervisionScheduleEntity("ss3");

        var selfStudyCount = ss1.day.countDistinct();
        var leaveSeatCount = ss2.day.countDistinct();
        var seventhPeriodCount = ss3.day.countDistinct();

        return queryFactory
                .select(teacher.id, teacher.name, selfStudyCount, leaveSeatCount, seventhPeriodCount)
                .from(teacher)
                .leftJoin(ss1).on(ss1.teacher.eq(teacher)
                        .and(ss1.type.eq(SupervisionType.SELF_STUDY_SUPERVISION)))
                .leftJoin(ss2).on(ss2.teacher.eq(teacher)
                        .and(ss2.type.eq(SupervisionType.LEAVE_SEAT_SUPERVISION)))
                .leftJoin(ss3).on(ss3.teacher.eq(teacher)
                        .and(ss3.type.eq(SupervisionType.SEVENTH_PERIOD_SUPERVISION)))
                .groupBy(teacher.id, teacher.name)
                .orderBy(selfStudyCount.add(leaveSeatCount).add(seventhPeriodCount).desc())
                .fetch();
    }
}