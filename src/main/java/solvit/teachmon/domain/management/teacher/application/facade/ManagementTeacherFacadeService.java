package solvit.teachmon.domain.management.teacher.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.teacher.domain.entity.SupervisionBanDayEntity;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherUpdateRequest;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.WeekDay;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementTeacherFacadeService {
    private final TeacherRepository teacherRepository;
    private final SupervisionScheduleRepository supervisionScheduleRepository;
    private final SupervisionBanDayRepository supervisionBanDayRepository;

    @Transactional(readOnly = true)
    public List<TeacherListResponse> getAllTeachers(String query) {
        return supervisionScheduleRepository.countTeacherSupervision(query);
    }

    @Transactional
    public void updateTeacher(TeacherUpdateRequest updateRequest, Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(TeacherNotFoundException::new);

        teacher.changeRole(updateRequest.role());
        teacher.changeName(updateRequest.name());
    }

    @Transactional
    public void deleteTeacher(Long teacherId) {
        if (!teacherRepository.existsById(teacherId)) {
            throw new TeacherNotFoundException();
        }
        teacherRepository.deleteById(teacherId);
    }

    @Transactional
    public void setTeacherBanDays(Long teacherId, List<WeekDay> banDays) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(TeacherNotFoundException::new);

        supervisionBanDayRepository.deleteAllByTeacherId(teacherId);

        List<SupervisionBanDayEntity> banDayEntities = new ArrayList<>();

        for (WeekDay banDay : banDays) {
            banDayEntities.add(SupervisionBanDayEntity.builder()
                    .teacher(teacher)
                    .weekDay(banDay)
                    .build());
        }

        supervisionBanDayRepository.saveAll(banDayEntities);
    }
}