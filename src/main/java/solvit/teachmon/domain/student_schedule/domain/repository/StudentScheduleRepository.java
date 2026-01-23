package solvit.teachmon.domain.student_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

import java.util.List;
import java.util.Map;

@Repository
public interface StudentScheduleRepository extends JpaRepository<StudentScheduleEntity, Long>, StudentScheduleQueryDslRepository {
}
