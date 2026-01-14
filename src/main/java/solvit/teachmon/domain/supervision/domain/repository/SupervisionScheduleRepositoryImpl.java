package solvit.teachmon.domain.supervision.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.QTeacherListResponse;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;


import java.util.List;

import static solvit.teachmon.domain.supervision.domain.entity.QSupervisionScheduleEntity.supervisionScheduleEntity;

@Repository
@RequiredArgsConstructor
public class SupervisionScheduleRepositoryImpl implements SupervisionScheduleRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeacherListResponse> countTeacherSupervision() {
        return queryFactory
                .select(new QTeacherListResponse(
                        supervisionScheduleEntity.teacher.id,
                        supervisionScheduleEntity.teacher.role,
                        supervisionScheduleEntity.teacher.name,
                        supervisionScheduleEntity.teacher.mail,
                        supervisionScheduleEntity.id.count().intValue()
                ))
                .from(supervisionScheduleEntity)
                .join(supervisionScheduleEntity.teacher)
                .groupBy(supervisionScheduleEntity.teacher.id)
                .fetch();
    }
}
