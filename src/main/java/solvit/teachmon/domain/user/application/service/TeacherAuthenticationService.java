package solvit.teachmon.domain.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.service.TeacherService;
import solvit.teachmon.domain.user.domain.service.TeacherValidateService;

@Service
@RequiredArgsConstructor
public class TeacherAuthenticationService {
    private final TeacherService teacherService;
    private final TeacherValidateService teacherValidateService;

    @Transactional
    public Role getRole(TeachmonOAuth2UserInfo teachmonOAuth2UserInfo) {
        return teacherValidateService.validateByProviderIdAndOAuth2Type(
                teachmonOAuth2UserInfo.providerId(),
                teachmonOAuth2UserInfo.oAuth2Type()
        ).map(teacher -> {
            teacher.update(teachmonOAuth2UserInfo.name(), teachmonOAuth2UserInfo.profile());
            return teacher.getRole();
        }).orElseGet(() -> {
            teacherService.signup(teachmonOAuth2UserInfo);
            return Role.TEACHER;
        });
    }
}
