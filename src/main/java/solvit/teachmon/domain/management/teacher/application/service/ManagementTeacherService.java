package solvit.teachmon.domain.management.teacher.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherUpdateRequest;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.annotation.Trace;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementTeacherService {
    private final SupervisionBanDayRepository supervisionBanDayRepository;

    @Trace
    public List<WeekDay> getTeacherBanDay(Long teacherId) {
        return supervisionBanDayRepository.findWeekDaysByTeacherId(teacherId);
    }
}
