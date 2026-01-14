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
import solvit.teachmon.global.annotation.Trace;
import solvit.teachmon.global.enums.WeekDay;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementTeacherFacadeService {
    private final TeacherRepository teacherRepository;
    private final SupervisionScheduleRepository supervisionScheduleRepository;
    private final SupervisionBanDayRepository supervisionBanDayRepository;

    @Trace
    public List<TeacherListResponse> getAllTeachers(String query) {
        return supervisionScheduleRepository.countTeacherSupervision(query);
    }

    @Trace
    @Transactional
    public void updateTeacher(TeacherUpdateRequest updateRequest, Long teacherId) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 교사를 찾을 수 없습니다"));

        teacher.changeRole(updateRequest.role());
        teacher.changeName(updateRequest.name());
    }

    @Trace
    @Transactional
    public void deleteTeacher(Long teacherId) {
        teacherRepository.deleteById(teacherId);
    }

    @Trace
    @Transactional
    public void setTeacherBanDay(Long teacherId, List<WeekDay> banDays) {
        TeacherEntity teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 교사를 찾을 수 없습니다"));

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