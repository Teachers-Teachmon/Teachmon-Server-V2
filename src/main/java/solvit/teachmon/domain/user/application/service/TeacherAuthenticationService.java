package solvit.teachmon.domain.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.oauth2.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.service.TeacherService;
import solvit.teachmon.domain.user.domain.service.TeacherValidateService;

@Service
@RequiredArgsConstructor
public class TeacherAuthenticationService {
    private final TeacherService teacherService;
    private final TeacherValidateService teacherValidateService;

    @Transactional
    public void ensureTeacherExists(TeachmonOAuth2UserInfo teachmonOAuth2UserInfo) {
        teacherValidateService.validateByProviderIdAndOAuth2Type(
                teachmonOAuth2UserInfo.providerId(),
                teachmonOAuth2UserInfo.oAuth2Type()
        ).ifPresentOrElse(
                teacher -> teacher.update(teachmonOAuth2UserInfo.name(), teachmonOAuth2UserInfo.profile()),
                () -> teacherService.signup(teachmonOAuth2UserInfo)
        );
    }
}
