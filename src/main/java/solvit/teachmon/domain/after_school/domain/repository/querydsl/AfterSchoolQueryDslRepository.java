package solvit.teachmon.domain.after_school.domain.repository.querydsl;

import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;

import java.util.List;
import java.util.Optional;

public interface AfterSchoolQueryDslRepository {
    List<AfterSchoolEntity> findByTeacherWithSchedules(TeacherEntity teacher);
    List<AfterSchoolEntity> findByPlaceWithSchedules(PlaceEntity place);
    Optional<AfterSchoolEntity> findWithAllRelations(Long afterSchoolId);
    List<TeacherEntity> findTeachersInBulk(List<Long> teacherIds);
    List<PlaceEntity> findPlacesInBulk(List<Long> placeIds);
    boolean existsByTeacherAndPlace(TeacherEntity teacher, PlaceEntity place);
}