package solvit.teachmon.domain.supervision.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.QTeacherListResponse;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.application.dto.QTeacherSupervisionCountDto;
import solvit.teachmon.domain.supervision.application.dto.TeacherSupervisionCountDto;
import solvit.teachmon.domain.supervision.domain.entity.QSupervisionScheduleEntity;
import solvit.teachmon.domain.user.domain.entity.QTeacherEntity;


import java.util.List;

@Repository
@RequiredArgsConstructor
public class SupervisionScheduleRepositoryImpl implements SupervisionScheduleRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeacherSupervisionCountDto> countTeacherSupervision(String query) {
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;
        QSupervisionScheduleEntity schedule = QSupervisionScheduleEntity.supervisionScheduleEntity;

        return queryFactory
                .select(new QTeacherSupervisionCountDto(
                        teacher.id,
                        teacher.role,
                        teacher.name,
                        teacher.mail,
                        teacher.profile,
                        schedule.id.count().intValue()
                ))
                .from(teacher)
                .leftJoin(schedule).on(schedule.teacher.eq(teacher))
                .where(nameContains(query))
                .groupBy(teacher.id)
                .fetch();
    }

    private BooleanExpression nameContains(String query) {
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;
        return query != null && !query.isBlank()
                ? teacher.name.containsIgnoreCase(query)
                : null;
    }
}
