package solvit.teachmon.global.security.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.service.TeacherValidateService;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

@Service
@RequiredArgsConstructor
public class TeachmonUserDetailsService implements UserDetailsService {
    private final TeacherValidateService teacherValidateService;

    @NonNull
    @Override
    public TeachmonUserDetails loadUserByUsername(@NonNull String nickname) throws TeacherNotFoundException {
        TeacherEntity teacher = teacherValidateService.validateByMail(nickname);
        return new TeachmonUserDetails(teacher);
    }
}
