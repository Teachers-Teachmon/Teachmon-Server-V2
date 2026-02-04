package solvit.teachmon.domain.management.student.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    @Query("SELECT s FROM StudentEntity s WHERE s.year = :year")
    List<StudentEntity> findByYear(Integer year);
}
