package solvit.teachmon.global.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@DisplayName("티치몬 유저 정보 테스트")
class TeachmonUserDetailsTest {

    private TeacherEntity teacher;
    private TeachmonUserDetails userDetails;

    @BeforeEach
    void setUp() {
        teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("수학 선생님")
                .build();
        userDetails = new TeachmonUserDetails(teacher);
    }

    @Test
    @DisplayName("선생님 권한이 올바르게 설정된다")
    void shouldHaveCorrectAuthorities() {
        // When: 권한을 조회하면
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Then: 선생님 권한이 설정되어 있다
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_TEACHER");
    }

    @Test
    @DisplayName("사용자 이름이 선생님 이름과 같다")
    void shouldReturnTeacherNameAsUsername() {
        // When: 사용자 이름을 조회하면
        String username = userDetails.getUsername();

        // Then: 선생님 이름이 반환된다
        assertThat(username).isEqualTo("김선생");
    }

    @Test
    @DisplayName("패스워드는 없다")
    void shouldReturnNullPassword() {
        // When: 패스워드를 조회하면
        String password = userDetails.getPassword();

        // Then: null이 반환된다
        assertThat(password).isNull();
    }

    @Test
    @DisplayName("활성 상태가 올바르게 반영된다")
    void shouldReturnCorrectEnabledStatus() {
        // When: 계정 활성 상태를 조회하면
        boolean enabled = userDetails.isEnabled();

        // Then: 선생님의 활성 상태와 같다
        assertThat(enabled).isEqualTo(teacher.getIsActive());
    }
}