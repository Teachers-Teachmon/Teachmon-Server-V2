package solvit.teachmon.domain.management.teacher.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.teacher.application.mapper.TeacherMapper;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.management.teacher.exception.TeacherAlreadyExistsException;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherRequest;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementTeacherService {
    private final SupervisionBanDayRepository supervisionBanDayRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;

    @Transactional
    public void createTeacher(TeacherRequest request) {
        if (teacherRepository.findByMail(request.email()).isPresent()) {
            throw new TeacherAlreadyExistsException();
        }

        TeacherEntity teacher = teacherMapper.toEntity(request);
        teacher.changeRole(request.role());

        teacherRepository.save(teacher);
    }

    @Transactional(readOnly = true)
    public List<WeekDay> getTeacherBanDays(Long teacherId) {
        return supervisionBanDayRepository.findAllWeekDaysByTeacherId(teacherId);
    }
}
