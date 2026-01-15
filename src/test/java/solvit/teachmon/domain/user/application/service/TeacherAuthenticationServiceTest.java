package solvit.teachmon.domain.user.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.service.TeacherService;
import solvit.teachmon.domain.user.domain.service.TeacherValidateService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TeacherAuthenticationServiceTest {

    @InjectMocks
    private TeacherAuthenticationService teacherAuthenticationService;

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherValidateService teacherValidateService;

    @Test
    @DisplayName("기존 사용자인 경우 정보를 업데이트하고 역할을 반환한다")
    void getRoleForExistingUser() {
        // given
        TeachmonOAuth2UserInfo userInfo = new TeachmonOAuth2UserInfo(
                "12345",  // providerId
                "test@example.com",  // mail
                "https://example.com/new-profile.jpg",  // profile
                "Updated Name",  // name
                OAuth2Type.GOOGLE  // oAuth2Type
        );
        TeacherEntity existingTeacher = TeacherEntity.builder()
                .mail("test@example.com")
                .name("Old Name")
                .oAuth2Type(OAuth2Type.GOOGLE)
                .providerId("12345")
                .profile("https://example.com/old-profile.jpg")
                .build();

        given(teacherValidateService.validateByProviderIdAndOAuth2Type("12345", OAuth2Type.GOOGLE))
                .willReturn(Optional.of(existingTeacher));

        // when
        Role result = teacherAuthenticationService.getRole(userInfo);

        // then
        assertThat(result).isEqualTo(Role.TEACHER);
        then(teacherService).should(never()).signup(userInfo);
    }

    @Test
    @DisplayName("신규 사용자인 경우 회원가입하고 TEACHER 역할을 반환한다")
    void getRoleForNewUser() {
        // given
        TeachmonOAuth2UserInfo userInfo = new TeachmonOAuth2UserInfo(
                "67890",  // providerId
                "new@example.com",  // mail
                "https://example.com/profile.jpg",  // profile
                "New User",  // name
                OAuth2Type.GOOGLE  // oAuth2Type
        );

        given(teacherValidateService.validateByProviderIdAndOAuth2Type("67890", OAuth2Type.GOOGLE))
                .willReturn(Optional.empty());

        // when
        Role result = teacherAuthenticationService.getRole(userInfo);

        // then
        assertThat(result).isEqualTo(Role.TEACHER);
        then(teacherService).should(times(1)).signup(userInfo);
    }
}
