package solvit.teachmon.domain.auth.infrastructure.security.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.auth.exception.UnsupportedAccountException;
import solvit.teachmon.domain.auth.infrastructure.security.strategy.OAuth2StrategyComposite;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2User;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeachmonOAuth2UserFacade extends DefaultOAuth2UserService {
    private final TeacherRepository teacherRepository;
    private final OAuth2StrategyComposite oAuth2StrategyComposite;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        OAuth2Type oAuth2Type = OAuth2Type.of(userRequest.getClientRegistration().getRegistrationId());
        TeachmonOAuth2UserInfo teachmonOAuth2UserInfo = oAuth2StrategyComposite.getOAuth2Strategy(oAuth2Type).getUserInfo(oauth2User);

        Optional<TeacherEntity> teacherEntityOptional = teacherRepository.findByProviderIdAndOAuth2Type(teachmonOAuth2UserInfo.providerId(), teachmonOAuth2UserInfo.oAuth2Type());
        if(teacherEntityOptional.isEmpty()) throw new UnsupportedAccountException();

        TeacherEntity teacherEntity = teacherEntityOptional.get();

        return TeachmonOAuth2User.builder()
                .role(teacherEntity.getRole())
                .mail(teachmonOAuth2UserInfo.mail())
                .attributes(oauth2User.getAttributes())
                .build();
    }
}
