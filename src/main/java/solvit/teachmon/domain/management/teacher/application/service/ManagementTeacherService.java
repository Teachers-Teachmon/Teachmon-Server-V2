package solvit.teachmon.domain.management.teacher.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
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
