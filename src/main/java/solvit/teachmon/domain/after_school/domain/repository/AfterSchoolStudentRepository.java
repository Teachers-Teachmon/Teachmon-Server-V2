package solvit.teachmon.domain.after_school.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolStudentEntity;

import java.util.List;

public interface AfterSchoolStudentRepository extends JpaRepository<AfterSchoolStudentEntity, Long> {
    
    @Query("SELECT ast FROM AfterSchoolStudentEntity ast " +
           "JOIN FETCH ast.student s " +
           "WHERE ast.afterSchool.id = :afterSchoolId")
    List<AfterSchoolStudentEntity> findByAfterSchoolIdWithStudent(@Param("afterSchoolId") Long afterSchoolId);
    
    @Query("SELECT ast FROM AfterSchoolStudentEntity ast " +
           "JOIN FETCH ast.student s " +
           "WHERE ast.afterSchool.id IN :afterSchoolIds")
    List<AfterSchoolStudentEntity> findByAfterSchoolIdsWithStudent(@Param("afterSchoolIds") List<Long> afterSchoolIds);
    
    void deleteByAfterSchoolId(Long afterSchoolId);
}