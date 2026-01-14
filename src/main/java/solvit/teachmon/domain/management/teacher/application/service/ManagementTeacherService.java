package solvit.teachmon.domain.management.teacher.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.annotation.Trace;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementTeacherService {
    private final TeacherRepository teacherRepository;
    private final SupervisionScheduleRepository supervisionScheduleRepository;

    @Trace
    public List<TeacherListResponse> getAllTeachers() {
        return supervisionScheduleRepository.countTeacherSupervision();
    }
}
