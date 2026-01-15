package solvit.teachmon.domain.user.domain.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=test",
        "spring.security.oauth2.client.registration.google.client-secret=test",
        "spring.security.oauth2.client.registration.google.scope=email,profile",
        "spring.security.oauth2.client.registration.google.authorization-grant-type=authorization_code",
        "spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google"
})
@ActiveProfiles("test")
@Transactional
@DisplayName("선생님 저장소 테스트")
class TeacherRepositoryTest {

    @Autowired
    private TeacherRepository teacherRepository;

    @Test
    @DisplayName("메일로 선생님을 찾을 수 있다")
    void shouldFindTeacherByMail() {
        // Given: 선생님 정보가 데이터베이스에 저장되어 있을 때
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("수학 선생님")
                .providerId("google-12345")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        teacherRepository.save(teacher);

        // When: 메일로 선생님을 찾으면
        Optional<TeacherEntity> result = teacherRepository.findByMail("kim@teacher.com");

        // Then: 해당 선생님이 조회된다
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("김선생");
        assertThat(result.get().getMail()).isEqualTo("kim@teacher.com");
    }

    @Test
    @DisplayName("존재하지 않는 메일로 찾으면 빈 결과가 반환된다")
    void shouldReturnEmptyWhenTeacherNotExists() {
        // Given: 데이터베이스에 선생님이 없을 때
        
        // When: 존재하지 않는 메일로 선생님을 찾으면
        Optional<TeacherEntity> result = teacherRepository.findByMail("nomail@nomail.com");

        // Then: 빈 결과가 반환된다
        assertThat(result).isEmpty();
    }
}