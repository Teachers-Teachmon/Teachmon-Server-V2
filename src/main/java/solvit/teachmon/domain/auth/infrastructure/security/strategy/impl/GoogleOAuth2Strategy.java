package solvit.teachmon.domain.auth.infrastructure.security.strategy.impl;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.auth.exception.UnsupportedAccountException;
import solvit.teachmon.domain.auth.infrastructure.security.strategy.OAuth2Strategy;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;

import java.util.Map;

@Component
public class GoogleOAuth2Strategy implements OAuth2Strategy {

    @Override
    public OAuth2Type getOAuth2ProviderType() {
        return OAuth2Type.GOOGLE;
    }

    @Override
    public TeachmonOAuth2UserInfo getUserInfo(OAuth2User user) {
        Map<String, Object> attributes = user.getAttributes();
        String providerId = attributes.get("sub").toString();
        String mail =  attributes.get("email").toString();
        String profile = attributes.get("picture").toString();
        String name = attributes.get("name").toString();

        if (!isAllowedEmail(mail)) {
            throw new UnsupportedAccountException();
        }

        return new TeachmonOAuth2UserInfo(providerId, mail, profile, name, OAuth2Type.GOOGLE);
    }

    private boolean isAllowedEmail(String email) {
        return email.matches("teacher\\d{3}@bssm\\.hs\\.kr") ||
               email.equals("hwansi@bssm.hs.kr") ||
               email.equals("teachmon08@gmail.com");
    }
}
