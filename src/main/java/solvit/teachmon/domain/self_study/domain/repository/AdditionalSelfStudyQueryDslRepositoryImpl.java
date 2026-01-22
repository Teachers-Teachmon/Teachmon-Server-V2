package solvit.teachmon.domain.self_study.domain.repository;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.self_study.domain.entity.QAdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;
import solvit.teachmon.domain.self_study.presentation.dto.response.QAdditionalSelfStudyGetResponse;
import solvit.teachmon.domain.self_study.presentation.dto.response.QAdditionalSelfStudyPeriodResponse;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdditionalSelfStudyQueryDslRepositoryImpl implements AdditionalSelfStudyQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<AdditionalSelfStudyGetResponse> findGroupedByDayAndGrade(Integer year) {
        QAdditionalSelfStudyEntity additionalSelfStudy = QAdditionalSelfStudyEntity.additionalSelfStudyEntity;

        return queryFactory
                .select(additionalSelfStudy.day,
                        additionalSelfStudy.grade,
                        additionalSelfStudy.id,
                        additionalSelfStudy.period)
                .from(additionalSelfStudy)
                .where(additionalSelfStudy.day.year().eq(year))
                .transform(GroupBy.groupBy(additionalSelfStudy.day, additionalSelfStudy.grade)
                        .list(new QAdditionalSelfStudyGetResponse(
                                additionalSelfStudy.day,
                                additionalSelfStudy.grade,
                                GroupBy.list(new QAdditionalSelfStudyPeriodResponse(
                                        additionalSelfStudy.id,
                                        additionalSelfStudy.period
                                ))
                        ))
                );
    }
}
