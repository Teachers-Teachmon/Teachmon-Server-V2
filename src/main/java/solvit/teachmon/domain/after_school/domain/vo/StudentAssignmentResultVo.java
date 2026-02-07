package solvit.teachmon.domain.after_school.domain.vo;

import lombok.Builder;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

import java.util.List;

@Builder
public record StudentAssignmentResultVo(AfterSchoolEntity afterSchool, List<StudentEntity> addedStudents, List<StudentEntity> removedStudents) {
}
