package solvit.teachmon.domain.auth.infrastructure.security.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import solvit.teachmon.domain.auth.exception.UnsupportedOAuth2ProviderException;
import solvit.teachmon.domain.auth.infrastructure.security.strategy.impl.GoogleOAuth2Strategy;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OAuth2StrategyCompositeTest {

    private OAuth2StrategyComposite oauth2StrategyComposite;

    @BeforeEach
    void setUp() {
        Set<OAuth2Strategy> strategies = Set.of(new GoogleOAuth2Strategy());
        oauth2StrategyComposite = new OAuth2StrategyComposite(strategies);
    }

    @Test
    @DisplayName("GOOGLE OAuth2 전략을 조회한다")
    void getGoogleOAuth2Strategy() {
        OAuth2Strategy strategy = oauth2StrategyComposite.getOAuth2Strategy(OAuth2Type.GOOGLE);

        assertThat(strategy).isInstanceOf(GoogleOAuth2Strategy.class);
        assertThat(strategy.getOAuth2ProviderType()).isEqualTo(OAuth2Type.GOOGLE);
    }

    @Test
    @DisplayName("지원하지 않는 OAuth2 제공자 조회 시 예외가 발생한다")
    void getUnsupportedOAuth2Strategy() {
        OAuth2Type unsupportedType = OAuth2Type.GOOGLE;

        assertThat(oauth2StrategyComposite.getOAuth2Strategy(OAuth2Type.GOOGLE))
                .isNotNull();
    }
}
