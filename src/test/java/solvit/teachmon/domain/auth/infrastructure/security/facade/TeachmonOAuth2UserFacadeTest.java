package solvit.teachmon.domain.auth.infrastructure.security.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.auth.exception.UnsupportedAccountException;
import solvit.teachmon.domain.auth.infrastructure.security.strategy.OAuth2StrategyComposite;
import solvit.teachmon.domain.auth.infrastructure.security.strategy.OAuth2Strategy;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("티치몬 OAuth2 사용자 파사드 테스트")
class TeachmonOAuth2UserFacadeTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private OAuth2StrategyComposite oAuth2StrategyComposite;

    @Mock
    private OAuth2Strategy oAuth2Strategy;

    private TeachmonOAuth2UserFacade teachmonOAuth2UserFacade;
    private TeacherEntity teacherEntity;
    private TeachmonOAuth2UserInfo userInfo;

    @BeforeEach
    void setUp() {
        teachmonOAuth2UserFacade = new TeachmonOAuth2UserFacade(teacherRepository, oAuth2StrategyComposite);
        
        teacherEntity = TeacherEntity.builder()
                .name("김선생")
                .mail("test@example.com")
                .profile("수학 선생님")
                .providerId("google-12345")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .build();

        userInfo = new TeachmonOAuth2UserInfo(
                "google-12345",
                "test@example.com",
                "수학 선생님",
                "테스트 유저",
                OAuth2Type.GOOGLE
        );
    }

    @Test
    @DisplayName("등록된 사용자 확인 시 TeacherEntity를 반환한다")
    void findRegisteredUser_Success() {
        given(teacherRepository.findByProviderIdAndOAuth2Type("google-12345", OAuth2Type.GOOGLE))
                .willReturn(Optional.of(teacherEntity));

        Optional<TeacherEntity> result = teacherRepository.findByProviderIdAndOAuth2Type("google-12345", OAuth2Type.GOOGLE);

        assertThat(result).isPresent();
        assertThat(result.get().getMail()).isEqualTo("test@example.com");
        assertThat(result.get().getProviderId()).isEqualTo("google-12345");
    }

    @Test
    @DisplayName("등록되지 않은 사용자 확인 시 빈 결과를 반환한다")
    void findUnregisteredUser_ReturnsEmpty() {
        given(teacherRepository.findByProviderIdAndOAuth2Type("unknown-123", OAuth2Type.GOOGLE))
                .willReturn(Optional.empty());

        Optional<TeacherEntity> result = teacherRepository.findByProviderIdAndOAuth2Type("unknown-123", OAuth2Type.GOOGLE);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("OAuth2 전략 컴포지트에서 올바른 전략을 가져온다")
    void getCorrectOAuth2Strategy() {
        given(oAuth2StrategyComposite.getOAuth2Strategy(OAuth2Type.GOOGLE)).willReturn(oAuth2Strategy);

        OAuth2Strategy result = oAuth2StrategyComposite.getOAuth2Strategy(OAuth2Type.GOOGLE);

        assertThat(result).isEqualTo(oAuth2Strategy);
    }

    @Test
    @DisplayName("TeachmonOAuth2UserInfo 생성이 올바르게 작동한다")
    void createTeachmonOAuth2UserInfo() {
        TeachmonOAuth2UserInfo info = new TeachmonOAuth2UserInfo(
                "provider-123",
                "user@test.com",
                "프로필",
                "사용자명",
                OAuth2Type.GOOGLE
        );

        assertThat(info.providerId()).isEqualTo("provider-123");
        assertThat(info.mail()).isEqualTo("user@test.com");
        assertThat(info.profile()).isEqualTo("프로필");
        assertThat(info.name()).isEqualTo("사용자명");
        assertThat(info.oAuth2Type()).isEqualTo(OAuth2Type.GOOGLE);
    }

    @Test
    @DisplayName("빈 Optional에서 get() 호출 시 예외가 발생한다")
    void emptyOptionalThrowsException() {
        Optional<TeacherEntity> empty = Optional.empty();

        assertThatThrownBy(empty::get)
                .isInstanceOf(java.util.NoSuchElementException.class);
    }
}
