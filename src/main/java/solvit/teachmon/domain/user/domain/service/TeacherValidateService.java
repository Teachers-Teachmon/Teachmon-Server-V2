package solvit.teachmon.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.OAuth2Type;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherValidateService {
    private final TeacherRepository teacherRepository;

    public TeacherEntity validateByMail(String mail) {
        return teacherRepository.findByMail(mail).orElseThrow(TeacherNotFoundException::new);
    }

    public Optional<TeacherEntity> validateByProviderIdAndOAuth2Type(String providerId, OAuth2Type oauth2Type) {
        return teacherRepository.findByProviderIdAndOAuth2Type(providerId, oauth2Type);
    }
}
