package solvit.teachmon.global.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.service.TeacherValidateService;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("티치몬 유저 정보 서비스 테스트")
class TeachmonUserDetailsServiceTest {

    @Mock
    private TeacherValidateService teacherValidateService;

    @InjectMocks
    private TeachmonUserDetailsService userDetailsService;

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
    @DisplayName("닉네임으로 유저 정보를 가져올 수 있다")
    void shouldLoadUserByUsername() {
        // Given: 선생님이 존재할 때
        given(teacherValidateService.validateByName("김선생")).willReturn(teacher);

        // When: 닉네임으로 유저 정보를 조회하면
        TeachmonUserDetails result = userDetailsService.loadUserByUsername("김선생");

        // Then: 해당 선생님의 유저 정보가 반환된다
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("김선생");
        assertThat(result.teacherEntity()).isEqualTo(teacher);
    }

    @Test
    @DisplayName("존재하지 않는 닉네임으로 조회하면 예외가 발생한다")
    void shouldThrowExceptionWhenTeacherNotFound() {
        // Given: 선생님이 존재하지 않을 때
        given(teacherValidateService.validateByName("없는선생님"))
                .willThrow(new TeacherNotFoundException());

        // When & Then: 조회하면 예외가 발생한다
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("없는선생님"))
                .isInstanceOf(TeacherNotFoundException.class);
    }
}