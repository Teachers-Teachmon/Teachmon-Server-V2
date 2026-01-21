package solvit.teachmon.domain.auth.infrastructure.security.strategy.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.OAuth2User;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GoogleOAuth2StrategyTest {

    private final GoogleOAuth2Strategy googleOAuth2Strategy = new GoogleOAuth2Strategy();

    @Test
    @DisplayName("OAuth2 제공자 타입으로 GOOGLE을 반환한다")
    void getOAuth2ProviderType() {
        // when
        OAuth2Type result = googleOAuth2Strategy.getOAuth2ProviderType();

        // then
        assertThat(result).isEqualTo(OAuth2Type.GOOGLE);
    }

    @Test
    @DisplayName("OAuth2User로부터 사용자 정보를 추출한다")
    void getUserInfo() {
        // given
        OAuth2User oauth2User = mock(OAuth2User.class);
        Map<String, Object> attributes = Map.of(
                "sub", "12345",
                "email", "test@gmail.com",
                "picture", "https://example.com/profile.jpg",
                "name", "Test User"
        );
        given(oauth2User.getAttributes()).willReturn(attributes);

        // when
        TeachmonOAuth2UserInfo result = googleOAuth2Strategy.getUserInfo(oauth2User);

        // then
        assertThat(result.providerId()).isEqualTo("12345");
        assertThat(result.mail()).isEqualTo("test@gmail.com");
        assertThat(result.profile()).isEqualTo("https://example.com/profile.jpg");
        assertThat(result.name()).isEqualTo("Test User");
        assertThat(result.oAuth2Type()).isEqualTo(OAuth2Type.GOOGLE);
    }
}
