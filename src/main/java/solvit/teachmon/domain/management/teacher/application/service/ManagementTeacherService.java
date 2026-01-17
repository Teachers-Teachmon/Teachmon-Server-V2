package solvit.teachmon.domain.management.teacher.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.global.enums.WeekDay;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementTeacherService {
    private final SupervisionBanDayRepository supervisionBanDayRepository;

    @Transactional(readOnly = true)
    public List<WeekDay> getTeacherBanDay(Long teacherId) {
        return supervisionBanDayRepository.findWeekDaysByTeacherId(teacherId);
    }
}
