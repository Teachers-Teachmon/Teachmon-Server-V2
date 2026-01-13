package solvit.teachmon.domain.user.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

@Service
@RequiredArgsConstructor
public class TeacherValidateService {
    private final TeacherRepository teacherRepository;

    @Transactional(readOnly = true)
    public TeacherEntity validateByName(String name) {
        return teacherRepository.findByName(name).orElseThrow(TeacherNotFoundException::new);
    }
}
