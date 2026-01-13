package solvit.teachmon.domain.user.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("선생님 저장소 테스트")
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    @DisplayName("이름으로 선생님을 찾을 수 있다")
    void shouldFindTeacherByName() {
        // Given: 선생님 정보가 데이터베이스에 저장되어 있을 때
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("수학 선생님")
                .build();
        teacherRepository.save(teacher);

        // When: 이름으로 선생님을 찾으면
        Optional<TeacherEntity> result = teacherRepository.findByName("김선생");

        // Then: 해당 선생님이 조회된다
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("김선생");
        assertThat(result.get().getMail()).isEqualTo("kim@teacher.com");
    }

    @Test
    @DisplayName("존재하지 않는 이름으로 찾으면 빈 결과가 반환된다")
    void shouldReturnEmptyWhenTeacherNotExists() {
        // Given: 데이터베이스에 선생님이 없을 때
        
        // When: 존재하지 않는 이름으로 선생님을 찾으면
        Optional<TeacherEntity> result = teacherRepository.findByName("없는선생님");

        // Then: 빈 결과가 반환된다
        assertThat(result).isEmpty();
    }
}