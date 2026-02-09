package solvit.teachmon.domain.management.student.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.querydsl.StudentQueryDslRepository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long>, StudentQueryDslRepository {

    @Query("SELECT s FROM StudentEntity s WHERE s.number IN :numbers")
    List<StudentEntity> findAllByNumberIn(@Param("numbers") List<Integer> numbers);

    @Query("SELECT s FROM StudentEntity s WHERE s.year = :year AND s.number IN :numbers")
    List<StudentEntity> findAllByYearAndNumberIn(@Param("year") Integer year, @Param("numbers") List<Integer> numbers);

    @Query("SELECT COUNT(s) > 0 FROM StudentEntity s WHERE s.number = :number AND s.name = :name")
    boolean existsByNumberAndName(@Param("number") Integer number, @Param("name") String name);

    @Query("SELECT s FROM StudentEntity s WHERE s.year = :year")
    List<StudentEntity> findByYear(Integer year);
}
