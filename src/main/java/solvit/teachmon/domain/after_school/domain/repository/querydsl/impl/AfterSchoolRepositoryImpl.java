package solvit.teachmon.domain.after_school.domain.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.QAfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.querydsl.AfterSchoolQueryDslRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.entity.QPlaceEntity;
import solvit.teachmon.domain.user.domain.entity.QTeacherEntity;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AfterSchoolRepositoryImpl implements AfterSchoolQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<AfterSchoolEntity> findByTeacherWithSchedules(TeacherEntity teacher) {
        return queryFactory.selectFrom(QAfterSchoolEntity.afterSchoolEntity)
                .leftJoin(QAfterSchoolEntity.afterSchoolEntity.afterSchoolSchedules)
                .fetchJoin()
                .where(QAfterSchoolEntity.afterSchoolEntity.teacher.eq(teacher))
                .fetch();
    }

    @Override
    public List<AfterSchoolEntity> findByPlaceWithSchedules(PlaceEntity place) {
        return queryFactory.selectFrom(QAfterSchoolEntity.afterSchoolEntity)
                .leftJoin(QAfterSchoolEntity.afterSchoolEntity.afterSchoolSchedules)
                .fetchJoin()
                .where(QAfterSchoolEntity.afterSchoolEntity.place.eq(place))
                .fetch();
    }

    @Override
    public Optional<AfterSchoolEntity> findWithAllRelations(Long afterSchoolId) {
        AfterSchoolEntity result = queryFactory.selectFrom(QAfterSchoolEntity.afterSchoolEntity)
                .join(QAfterSchoolEntity.afterSchoolEntity.teacher, QTeacherEntity.teacherEntity)
                .fetchJoin()
                .join(QAfterSchoolEntity.afterSchoolEntity.place, QPlaceEntity.placeEntity)
                .fetchJoin()
                .leftJoin(QAfterSchoolEntity.afterSchoolEntity.afterSchoolSchedules)
                .fetchJoin()
                .where(QAfterSchoolEntity.afterSchoolEntity.id.eq(afterSchoolId))
                .fetchOne();
        
        return Optional.ofNullable(result);
    }

    @Override
    public List<TeacherEntity> findTeachersInBulk(List<Long> teacherIds) {
        return queryFactory.selectFrom(QTeacherEntity.teacherEntity)
                .where(QTeacherEntity.teacherEntity.id.in(teacherIds))
                .fetch();
    }

    @Override
    public List<PlaceEntity> findPlacesInBulk(List<Long> placeIds) {
        return queryFactory.selectFrom(QPlaceEntity.placeEntity)
                .where(QPlaceEntity.placeEntity.id.in(placeIds))
                .fetch();
    }

    @Override
    public boolean existsByTeacherAndPlace(TeacherEntity teacher, PlaceEntity place) {
        Integer result = queryFactory.selectOne()
                .from(QAfterSchoolEntity.afterSchoolEntity)
                .where(
                    QAfterSchoolEntity.afterSchoolEntity.teacher.eq(teacher),
                    QAfterSchoolEntity.afterSchoolEntity.place.eq(place)
                )
                .fetchFirst();
        
        return result != null;
    }
}