package solvit.teachmon.domain.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.domain.user.presentation.dto.response.TeacherProfileResponseDto;
import solvit.teachmon.global.security.user.TeachmonUserDetails;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public TeacherProfileResponseDto getMyUserProfile(TeachmonUserDetails teachmonUserDetails) {
        return teacherRepository.findUserProfileById(teachmonUserDetails.getId()).orElseThrow(TeacherNotFoundException::new);
    }
}
