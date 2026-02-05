package solvit.teachmon.domain.after_school.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolStudentEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolStudentRepository;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolInfoException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AfterSchoolStudentDomainService {
    private final AfterSchoolStudentRepository afterSchoolStudentRepository;

    public void assignStudents(AfterSchoolEntity afterSchool, List<StudentEntity> students) {
        validateStudents(afterSchool, students);

        afterSchool.getAfterSchoolStudents().clear();

        if (students == null || students.isEmpty()) {
            return;
        }

        for (StudentEntity student : students) {
            afterSchool.getAfterSchoolStudents().add(
                    AfterSchoolStudentEntity.builder()
                            .afterSchool(afterSchool)
                            .student(student)
                            .build()
            );
        }
    }

    public void deleteAllByAfterSchool(AfterSchoolEntity afterSchool) {
        afterSchool.getAfterSchoolStudents().clear();
    }

    private void validateStudents(AfterSchoolEntity afterSchool, List<StudentEntity> students) {
        if (students == null || students.isEmpty()) {
            return;
        }

        Set<Long> studentIds = new HashSet<>((int) (students.size() / 0.75f) + 1);

        for (StudentEntity student : students) {
            if (!studentIds.add(student.getId())) {
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
    }
}
