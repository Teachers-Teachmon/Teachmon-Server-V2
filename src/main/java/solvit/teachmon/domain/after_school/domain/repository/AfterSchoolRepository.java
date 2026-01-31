package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.querydsl.AfterSchoolQueryDslRepository;

import java.util.List;

@Repository
public interface AfterSchoolRepository extends JpaRepository<AfterSchoolEntity, Long>, AfterSchoolQueryDslRepository {
    
    @Query("SELECT DISTINCT a FROM AfterSchoolEntity a " +
           "JOIN FETCH a.branch " +
           "JOIN FETCH a.teacher " +
           "JOIN FETCH a.place " +
           "LEFT JOIN FETCH a.afterSchoolStudents ass " +
           "LEFT JOIN FETCH ass.student")
    List<AfterSchoolEntity> findAllWithRelations();
    
    @Query("SELECT DISTINCT a FROM AfterSchoolEntity a " +
           "JOIN FETCH a.branch " +
           "JOIN FETCH a.teacher " +
           "JOIN FETCH a.place " +
           "LEFT JOIN FETCH a.afterSchoolStudents ass " +
           "LEFT JOIN FETCH ass.student " +
           "WHERE a.isEnd = false")
    List<AfterSchoolEntity> findActiveAfterSchoolsWithRelations();
}
