package solvit.teachmon.domain.after_school.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolStudentEntity;
import solvit.teachmon.domain.after_school.domain.vo.StudentAssignmentResultVo;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolInfoException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AfterSchoolStudentDomainService {

    public StudentAssignmentResultVo assignStudents(AfterSchoolEntity afterSchool, List<StudentEntity> students) {
        if (students == null) {
            students = List.of();
        }

        List<StudentEntity> newStudentIds = validateStudents(afterSchool, students);
        List<StudentEntity> currentStudentIds = afterSchool.getAfterSchoolStudents().stream()
                .map(AfterSchoolStudentEntity::getStudent)
                .toList();

        afterSchool.getAfterSchoolStudents().clear();

        List<StudentEntity> addedStudents = new ArrayList<>(newStudentIds);
        addedStudents.removeAll(currentStudentIds);

        List<StudentEntity> removedIds = new ArrayList<>(currentStudentIds);
        removedIds.removeAll(newStudentIds);

        for (StudentEntity student : students) {
            afterSchool.getAfterSchoolStudents().add(
                    AfterSchoolStudentEntity.builder()
                            .afterSchool(afterSchool)
                            .student(student)
                            .build()
            );
        }

        return StudentAssignmentResultVo.builder()
                .afterSchool(afterSchool)
                .addedStudents(addedStudents)
                .removedStudents(removedIds)
                .build();
    }

    public void deleteAllByAfterSchool(AfterSchoolEntity afterSchool) {
        afterSchool.getAfterSchoolStudents().clear();
    }

    private List<StudentEntity> validateStudents(AfterSchoolEntity afterSchool, List<StudentEntity> students) {
        if (students == null || students.isEmpty()) {
            return List.of();
        }

        Set<StudentEntity> studentSet = new HashSet<>((int) (students.size() / 0.75f) + 1);

        for (StudentEntity student : students) {
            if (!studentSet.add(student)) {
                throw new InvalidAfterSchoolInfoException(
                        String.format("학생 '%s'가 중복 등록되었습니다.", student.getName())
                );
            }

            if (!student.getGrade().equals(afterSchool.getGrade())) {
                throw new InvalidAfterSchoolInfoException(
                        String.format(
                                "학생 '%s'의 학년(%d)이 방과후 대상 학년(%d)과 일치하지 않습니다.",
                                student.getName(),
                                student.getGrade(),
                                afterSchool.getGrade()
                        )
                );
            }
        }

        return studentSet.stream().toList();
    }

}
