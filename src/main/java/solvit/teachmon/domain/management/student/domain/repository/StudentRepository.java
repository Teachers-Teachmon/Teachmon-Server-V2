package solvit.teachmon.domain.management.student.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
