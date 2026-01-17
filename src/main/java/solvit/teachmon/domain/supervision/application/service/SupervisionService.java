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

    /**
     * 교사별 감독 횟수를 조회합니다.
     *
     * @param query 교사 이름 검색 쿼리 (선택사항)
     * @return 교사 목록과 각 교사의 감독 횟수
     */
    public List<TeacherSupervisionCountDto> searchTeacherWithSupervisionCounts(String query) {
        return supervisionScheduleRepository.countTeacherSupervision(query);
    }
}
