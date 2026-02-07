package solvit.teachmon.domain.after_school.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolStudentEntity;
import solvit.teachmon.domain.after_school.domain.vo.StudentAssignmentResultVo;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolInfoException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("AfterSchoolStudentDomainService 테스트")
class AfterSchoolStudentDomainServiceTest {

    private final AfterSchoolStudentDomainService service = new AfterSchoolStudentDomainService();

    @Test
    @DisplayName("학생 배정 시 추가/삭제 학생 정보가 반환된다")
    void shouldReturnAddedAndRemovedStudents() {
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);
        given(afterSchool.getGrade()).willReturn(2);

        List<AfterSchoolStudentEntity> current = new ArrayList<>();
        StudentEntity existing = createStudent(2, 1, "기존학생");
        current.add(AfterSchoolStudentEntity.builder()
                .afterSchool(afterSchool)
                .student(existing)
                .build());
        given(afterSchool.getAfterSchoolStudents()).willReturn(current);

        StudentEntity newcomer = createStudent(2, 2, "신규학생");

        StudentAssignmentResultVo result = service.assignStudents(afterSchool, List.of(existing, newcomer));

        assertThat(result.addedStudents()).containsExactly(newcomer);
        assertThat(result.removedStudents()).isEmpty();
        assertThat(afterSchool.getAfterSchoolStudents()).hasSize(2);
    }

    @Test
    @DisplayName("방과후 학년과 다른 학생을 배정하면 예외가 발생한다")
    void shouldThrowWhenGradeMismatch() {
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);
        given(afterSchool.getGrade()).willReturn(2);

        StudentEntity invalidGradeStudent = createStudent(3, 1, "학년불일치");

        assertThatThrownBy(() -> service.assignStudents(afterSchool, List.of(invalidGradeStudent)))
                .isInstanceOf(InvalidAfterSchoolInfoException.class)
                .hasMessageContaining("학년");
    }

    @Test
    @DisplayName("중복 학생을 배정하면 예외가 발생한다")
    void shouldThrowWhenDuplicateStudent() {
        AfterSchoolEntity afterSchool = mock(AfterSchoolEntity.class);
        given(afterSchool.getGrade()).willReturn(2);

        StudentEntity student = createStudent(2, 1, "중복학생");

        assertThatThrownBy(() -> service.assignStudents(afterSchool, List.of(student, student)))
                .isInstanceOf(InvalidAfterSchoolInfoException.class)
                .hasMessageContaining("중복");
    }

    private StudentEntity createStudent(int grade, int number, String name) {
        return StudentEntity.builder()
                .year(2026)
                .grade(grade)
                .classNumber(1)
                .number(number)
                .name(name)
                .build();
    }
}
