package solvit.teachmon.domain.user.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 저장소 테스트")
class TeacherRepositoryTest {

    @Mock
    private TeacherRepository teacherRepository;

    private TeacherEntity teacher;

    @BeforeEach
    void setUp() {
        teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("수학 선생님")
                .providerId("google-12345")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
    }

    @Test
    @DisplayName("메일로 선생님을 찾을 수 있다")
    void shouldFindTeacherByMail() {
        given(teacherRepository.findByMail("kim@teacher.com")).willReturn(Optional.of(teacher));

        Optional<TeacherEntity> result = teacherRepository.findByMail("kim@teacher.com");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("김선생");
        assertThat(result.get().getMail()).isEqualTo("kim@teacher.com");
    }

    @Test
    @DisplayName("존재하지 않는 메일로 찾으면 빈 결과가 반환된다")
    void shouldReturnEmptyWhenTeacherNotExists() {
        given(teacherRepository.findByMail("nomail@nomail.com")).willReturn(Optional.empty());

        Optional<TeacherEntity> result = teacherRepository.findByMail("nomail@nomail.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Provider ID와 OAuth2 타입으로 선생님을 찾을 수 있다")
    void shouldFindTeacherByProviderIdAndOAuth2Type() {
        given(teacherRepository.findByProviderIdAndOAuth2Type("google-12345", OAuth2Type.GOOGLE))
                .willReturn(Optional.of(teacher));

        Optional<TeacherEntity> result = teacherRepository.findByProviderIdAndOAuth2Type("google-12345", OAuth2Type.GOOGLE);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("김선생");
        assertThat(result.get().getProviderId()).isEqualTo("google-12345");
        assertThat(result.get().getOAuth2Type()).isEqualTo(OAuth2Type.GOOGLE);
    }

    @Test
    @DisplayName("존재하지 않는 Provider ID로 찾으면 빈 결과가 반환된다")
    void shouldReturnEmptyWhenProviderIdNotExists() {
        given(teacherRepository.findByProviderIdAndOAuth2Type("non-existent", OAuth2Type.GOOGLE))
                .willReturn(Optional.empty());

        Optional<TeacherEntity> result = teacherRepository.findByProviderIdAndOAuth2Type("non-existent", OAuth2Type.GOOGLE);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("메일이 존재하는지 확인할 수 있다")
    void shouldCheckIfMailExists() {
        given(teacherRepository.findByMail("park@teacher.com")).willReturn(Optional.of(teacher));

        Optional<TeacherEntity> result = teacherRepository.findByMail("park@teacher.com");

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("존재하지 않는 메일은 빈 결과를 반환한다")
    void shouldReturnEmptyForNonExistentMail() {
        given(teacherRepository.findByMail("nonexistent@teacher.com")).willReturn(Optional.empty());

        Optional<TeacherEntity> result = teacherRepository.findByMail("nonexistent@teacher.com");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Repository 저장 기능이 정상 작동한다")
    void shouldSaveTeacher() {
        TeacherEntity newTeacher = TeacherEntity.builder()
                .name("이선생")
                .mail("lee@teacher.com")
                .profile("영어 선생님")
                .providerId("google-67890")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();
        given(teacherRepository.save(newTeacher)).willReturn(newTeacher);

        TeacherEntity savedTeacher = teacherRepository.save(newTeacher);

        assertThat(savedTeacher).isEqualTo(newTeacher);
        then(teacherRepository).should(times(1)).save(newTeacher);
    }
}
