package solvit.teachmon.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.auth.infrastructure.security.vo.TeachmonOAuth2UserInfo;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;

    @Transactional
    public void signup(TeachmonOAuth2UserInfo teachmonOAuth2UserInfo) {
        TeacherEntity teacherEntity = TeacherEntity.builder()
                .mail(teachmonOAuth2UserInfo.mail())
                .name(teachmonOAuth2UserInfo.name())
                .oAuth2Type(teachmonOAuth2UserInfo.oAuth2Type())
                .providerId(teachmonOAuth2UserInfo.providerId())
                .profile(teachmonOAuth2UserInfo.profile())
                .build();
        teacherRepository.save(teacherEntity);
    }
}
