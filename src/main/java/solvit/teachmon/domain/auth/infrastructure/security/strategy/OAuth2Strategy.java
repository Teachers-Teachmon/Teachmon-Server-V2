package solvit.teachmon.domain.oauth2.infrastructure.security.strategy;

import org.springframework.security.oauth2.core.user.OAuth2User;
import solvit.teachmon.domain.oauth2.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

public interface OAuth2Strategy {
    OAuth2Type getOAuth2ProviderType();

    TeachmonOAuth2UserInfo getUserInfo(OAuth2User user);
}
