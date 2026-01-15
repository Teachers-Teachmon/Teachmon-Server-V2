package solvit.teachmon.domain.user.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @InjectMocks
    private TeacherService teacherService;

    @Mock
    private TeacherRepository teacherRepository;

    @Test
    @DisplayName("OAuth2 사용자 정보로 교사를 회원가입 시킨다")
    void signup() {
        // given
        TeachmonOAuth2UserInfo userInfo = new TeachmonOAuth2UserInfo(
                "12345",  // providerId
                "test@example.com",  // mail
                "https://example.com/profile.jpg",  // profile
                "Test User",  // name
                OAuth2Type.GOOGLE  // oAuth2Type
        );

        // when
        teacherService.signup(userInfo);

        // then
        then(teacherRepository).should(times(1)).save(any(TeacherEntity.class));
    }
}
