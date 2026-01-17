package solvit.teachmon.domain.supervision.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.supervision.application.dto.TeacherSupervisionCountDto;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupervisionService {
    private final SupervisionScheduleRepository supervisionScheduleRepository;

    public List<TeacherSupervisionCountDto> getTeacherSupervisionCounts(String query) {
        return supervisionScheduleRepository.countTeacherSupervision(query);
    }
}
