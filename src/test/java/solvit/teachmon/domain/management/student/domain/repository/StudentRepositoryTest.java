package solvit.teachmon.domain.management.student.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("학생 저장소 테스트")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("ID로 학생을 찾을 수 있다")
    void shouldFindStudentById() {
        // Given: 학생 정보가 데이터베이스에 저장되어 있을 때
        StudentEntity student = StudentEntity.withCurrentYearBuilder()
                .grade(2)
                .classNumber(3)
                .number(15)
                .name("김철수")
                .build();
        StudentEntity savedStudent = studentRepository.save(student);

        // When: ID로 학생을 찾으면
        Optional<StudentEntity> result = studentRepository.findById(savedStudent.getId());

        // Then: 해당 학생이 조회된다
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("김철수");
        assertThat(result.get().getGrade()).isEqualTo(2);
        assertThat(result.get().getClassNumber()).isEqualTo(3);
        assertThat(result.get().getNumber()).isEqualTo(15);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 찾으면 빈 결과가 반환된다")
    void shouldReturnEmptyWhenStudentNotExists() {
        // Given: 데이터베이스에 학생이 없을 때
        Long nonExistentId = 999L;

        // When: 존재하지 않는 ID로 학생을 찾으면
        Optional<StudentEntity> result = studentRepository.findById(nonExistentId);

        // Then: 빈 결과가 반환된다
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("여러 ID로 학생들을 한 번에 조회할 수 있다")
    void shouldFindStudentsByMultipleIds() {
        // Given: 여러 학생이 데이터베이스에 저장되어 있을 때
        StudentEntity student1 = StudentEntity.withCurrentYearBuilder()
                .grade(1)
                .classNumber(1)
                .number(1)
                .name("김철수")
                .build();
        StudentEntity student2 = StudentEntity.withCurrentYearBuilder()
                .grade(2)
                .classNumber(2)
                .number(2)
                .name("이영희")
                .build();
        StudentEntity student3 = StudentEntity.withCurrentYearBuilder()
                .grade(3)
                .classNumber(3)
                .number(3)
                .name("박민수")
                .build();

        List<StudentEntity> savedStudents = studentRepository.saveAll(List.of(student1, student2, student3));
        List<Long> studentIds = savedStudents.stream().map(StudentEntity::getId).toList();

        // When: 여러 ID로 학생들을 조회하면
        List<StudentEntity> results = studentRepository.findAllById(studentIds);

        // Then: 모든 학생이 조회된다
        assertThat(results).hasSize(3);
        assertThat(results).extracting(StudentEntity::getName)
                .containsExactlyInAnyOrder("김철수", "이영희", "박민수");
    }

    @Test
    @DisplayName("학생을 저장할 수 있다")
    void shouldSaveStudent() {
        // Given: 새로운 학생 정보가 주어졌을 때
        StudentEntity student = StudentEntity.withYearBuilder()
                .year(2023)
                .grade(1)
                .classNumber(5)
                .number(20)
                .name("새학생")
                .build();

        // When: 학생을 저장하면
        StudentEntity savedStudent = studentRepository.save(student);

        // Then: 학생이 저장되고 ID가 생성된다
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo("새학생");
        assertThat(savedStudent.getYear()).isEqualTo(2023);
        assertThat(savedStudent.getGrade()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 학생을 한 번에 저장할 수 있다")
    void shouldSaveMultipleStudents() {
        // Given: 여러 학생 정보가 주어졌을 때
        List<StudentEntity> students = List.of(
                StudentEntity.withCurrentYearBuilder()
                        .grade(1).classNumber(1).number(1).name("학생1").build(),
                StudentEntity.withCurrentYearBuilder()
                        .grade(1).classNumber(1).number(2).name("학생2").build(),
                StudentEntity.withCurrentYearBuilder()
                        .grade(1).classNumber(1).number(3).name("학생3").build()
        );

        // When: 여러 학생을 한 번에 저장하면
        List<StudentEntity> savedStudents = studentRepository.saveAll(students);

        // Then: 모든 학생이 저장된다
        assertThat(savedStudents).hasSize(3);
        assertThat(savedStudents).allMatch(student -> student.getId() != null);
        assertThat(savedStudents).extracting(StudentEntity::getName)
                .containsExactly("학생1", "학생2", "학생3");
    }

    @Test
    @DisplayName("학생을 삭제할 수 있다")
    void shouldDeleteStudent() {
        // Given: 학생이 데이터베이스에 저장되어 있을 때
        StudentEntity student = StudentEntity.withCurrentYearBuilder()
                .grade(2)
                .classNumber(3)
                .number(15)
                .name("삭제될학생")
                .build();
        StudentEntity savedStudent = studentRepository.save(student);
        Long studentId = savedStudent.getId();

        // When: 학생을 삭제하면
        studentRepository.delete(savedStudent);

        // Then: 학생이 삭제된다
        Optional<StudentEntity> result = studentRepository.findById(studentId);
        assertThat(result).isEmpty();
    }
}