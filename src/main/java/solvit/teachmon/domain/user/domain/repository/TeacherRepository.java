package solvit.teachmon.domain.user.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, Long> {
    @Query("select t from TeacherEntity t where t.name = :name")
    Optional<TeacherEntity> findByName(String name);
}
