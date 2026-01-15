package solvit.teachmon.domain.oauth2.infrastructure.security.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.oauth2.exception.UnsupportedAccountException;
import solvit.teachmon.domain.oauth2.infrastructure.security.strategy.OAuth2StrategyComposite;
import solvit.teachmon.domain.oauth2.infrastructure.security.vo.TeachmonOAuth2User;
import solvit.teachmon.domain.oauth2.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.application.service.TeacherAuthenticationService;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.enums.Role;

@Service
@RequiredArgsConstructor
public class TeachmonOAuth2UserFacade extends DefaultOAuth2UserService {
    private final TeacherAuthenticationService teacherAuthenticationService;
    private final OAuth2StrategyComposite oAuth2StrategyComposite;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        OAuth2Type oAuth2Type = OAuth2Type.of(userRequest.getClientRegistration().getClientId());
        TeachmonOAuth2UserInfo teachmonOAuth2UserInfo = oAuth2StrategyComposite.getOAuth2Strategy(oAuth2Type).getUserInfo(oauth2User);
        checkAccount(teachmonOAuth2UserInfo.mail());
        Role role = teacherAuthenticationService.getRole(teachmonOAuth2UserInfo);

        return TeachmonOAuth2User.builder()
                .role(role)
                .mail(teachmonOAuth2UserInfo.mail())
                .attributes(oauth2User.getAttributes())
                .build();
    }

    private void checkAccount(String mail) {
        if(
                !mail.equals("teachmon08@gmail.com") &&
                !mail.matches("^teacher\\d{3}@bssm\\.hs\\.kr$") &&
                !mail.equals("hwansi@bssm.hs.kr")
        ) {
            throw new UnsupportedAccountException();
        }
    }
}
