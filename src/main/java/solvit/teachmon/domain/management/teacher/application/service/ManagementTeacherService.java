package solvit.teachmon.domain.management.teacher.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherUpdateRequest;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
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

    @Trace
    @Transactional
    public void updateTeacher(TeacherUpdateRequest updateRequest, Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 교사를 찾을 수 없습니다."));

        teacher.changeRole(updateRequest.role());
        teacher.changeName(updateRequest.name());
    }
}
