package solvit.teachmon.domain.user.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 검증 서비스 테스트")
class TeacherValidateServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherValidateService teacherValidateService;

    private TeacherEntity teacher;

    @BeforeEach
    void setUp() {
        teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("수학 선생님")
                .build();
    }

    @Test
    @DisplayName("이름으로 선생님을 검증할 수 있다")
    void shouldValidateTeacherByName() {
        // Given: 선생님이 존재할 때
        given(teacherRepository.findByName("김선생")).willReturn(Optional.of(teacher));

        // When: 이름으로 선생님을 검증하면
        TeacherEntity result = teacherValidateService.validateByName("김선생");

        // Then: 해당 선생님이 반환된다
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("김선생");
    }

    @Test
    @DisplayName("존재하지 않는 선생님을 검증하면 예외가 발생한다")
    void shouldThrowExceptionWhenTeacherNotExists() {
        // Given: 선생님이 존재하지 않을 때
        given(teacherRepository.findByName("없는선생님")).willReturn(Optional.empty());

        // When & Then: 검증하면 예외가 발생한다
        assertThatThrownBy(() -> teacherValidateService.validateByName("없는선생님"))
                .isInstanceOf(TeacherNotFoundException.class);
    }
}