package solvit.teachmon.domain.supervision.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.QTeacherListResponse;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.application.mapper.SupervisionResponseMapper;
import solvit.teachmon.domain.supervision.domain.entity.QSupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionSortOrder;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionRankResponseDto;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionScheduleResponseDto;
import solvit.teachmon.domain.user.domain.entity.QTeacherEntity;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SupervisionScheduleQueryDslRepositoryImpl implements SupervisionScheduleQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final SupervisionResponseMapper mapper;

    @Override
    public List<TeacherListResponse> countTeacherSupervision(String query) {
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;
        QSupervisionScheduleEntity schedule = QSupervisionScheduleEntity.supervisionScheduleEntity;

        return queryFactory
                .select(new QTeacherListResponse(
                        teacher.id,
                        teacher.role,
                        teacher.name,
                        teacher.mail,
                        schedule.day.countDistinct().intValue()
                ))
                .from(teacher)
                .leftJoin(schedule).on(schedule.teacher.eq(teacher))
                .where(teacherNameContains(teacher, query))
                .groupBy(teacher.id)
                .fetch();
    }

    @Override
    public List<SupervisionScheduleEntity> findByMonthAndQuery(Integer month, String query) {
        QSupervisionScheduleEntity schedule = QSupervisionScheduleEntity.supervisionScheduleEntity;
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;

        return queryFactory
                .selectFrom(schedule)
                .join(schedule.teacher, teacher).fetchJoin()
                .where(
                    monthEquals(month),
                    teacherNameContains(teacher, query)
                )
                .orderBy(schedule.day.asc(), schedule.period.asc(), schedule.type.asc())
                .fetch();
    }

    @Override
    public List<SupervisionScheduleResponseDto> findSchedulesGroupedByDayAndQuery(Integer month, String query) {
        // 먼저 데이터를 조회
        List<SupervisionScheduleEntity> schedules = findByMonthAndQuery(month, query);
        
        // SupervisionScheduleResponseDto로 변환
        return mapper.convertToResponseDtos(schedules);
    }


    private BooleanExpression monthEquals(Integer month) {
        QSupervisionScheduleEntity schedule = QSupervisionScheduleEntity.supervisionScheduleEntity;
        return month != null 
                ? schedule.day.month().eq(month)
                : null;
    }




    @Override
    public List<SupervisionRankResponseDto> findSupervisionRankings(String query, SupervisionSortOrder sortOrder) {
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;
        QSupervisionScheduleEntity schedule = QSupervisionScheduleEntity.supervisionScheduleEntity;

        // LEFT JOIN 3회(타입별 alias) → LEFT JOIN 1회 + 조건부 집계(CASE WHEN)로 개선
        // 기존: teacher × selfStudy × leaveSeat × seventhPeriod → Cartesian Product 발생
        // 개선: supervision_schedule 테이블을 1회만 읽고 타입별로 COUNT DISTINCT 집계
        var selfStudyCount = com.querydsl.core.types.dsl.Expressions.numberTemplate(
                Long.class,
                "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
                schedule.type, SupervisionType.SELF_STUDY_SUPERVISION, schedule.day);

        var leaveSeatCount = com.querydsl.core.types.dsl.Expressions.numberTemplate(
                Long.class,
                "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
                schedule.type, SupervisionType.LEAVE_SEAT_SUPERVISION, schedule.day);

        var seventhPeriodCount = com.querydsl.core.types.dsl.Expressions.numberTemplate(
                Long.class,
                "COUNT(DISTINCT CASE WHEN {0} = {1} THEN {2} END)",
                schedule.type, SupervisionType.SEVENTH_PERIOD_SUPERVISION, schedule.day);

        var totalCount = selfStudyCount.add(leaveSeatCount).add(seventhPeriodCount);

        var results = queryFactory
                .select(
                        teacher.name,
                        selfStudyCount,
                        leaveSeatCount,
                        seventhPeriodCount
                )
                .from(teacher)
                .leftJoin(schedule).on(schedule.teacher.eq(teacher))
                .where(teacherNameContains(teacher, query))
                .groupBy(teacher.id, teacher.name)
                .orderBy(sortOrder == SupervisionSortOrder.DESC ?
                        totalCount.desc() : totalCount.asc())
                .fetch();

        List<SupervisionRankResponseDto> rankList = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            var tuple = results.get(i);
            Long selfStudy = tuple.get(1, Long.class);
            Long leaveSeat = tuple.get(2, Long.class);
            Long seventhPeriod = tuple.get(3, Long.class);

            int selfStudyInt = selfStudy != null ? selfStudy.intValue() : 0;
            int leaveSeatInt = leaveSeat != null ? leaveSeat.intValue() : 0;
            int seventhPeriodInt = seventhPeriod != null ? seventhPeriod.intValue() : 0;

            rankList.add(SupervisionRankResponseDto.builder()
                    .rank(i + 1)
                    .name(tuple.get(teacher.name))
                    .selfStudySupervisionCount(selfStudyInt)
                    .leaveSeatSupervisionCount(leaveSeatInt)
                    .seventhPeriodSupervisionCount(seventhPeriodInt)
                    .totalSupervisionCount(selfStudyInt + leaveSeatInt + seventhPeriodInt)
                    .build());
        }

        return rankList;
    }
    
    private BooleanExpression teacherNameContains(QTeacherEntity teacher, String query) {
        return query != null && !query.isBlank() 
                ? teacher.name.containsIgnoreCase(query) 
                : null;
    }
}
