package solvit.teachmon.global.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

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
                .providerId("google-12345")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        userDetails = new TeachmonUserDetails(teacher);
    }

    @Test
    @DisplayName("선생님 권한이 올바르게 설정된다")
    void shouldHaveCorrectAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_TEACHER");
    }

    @Test
    @DisplayName("사용자 이름이 선생님 이름과 같다")
    void shouldReturnTeacherNameAsUsername() {
        String username = userDetails.getUsername();

        assertThat(username).isEqualTo("김선생");
    }

    @Test
    @DisplayName("패스워드는 없다")
    void shouldReturnNullPassword() {
        String password = userDetails.getPassword();

        assertThat(password).isNull();
    }

    @Test
    @DisplayName("활성 상태가 올바르게 반영된다")
    void shouldReturnCorrectEnabledStatus() {
        boolean enabled = userDetails.isEnabled();

        assertThat(enabled).isEqualTo(teacher.getIsActive());
    }
}
