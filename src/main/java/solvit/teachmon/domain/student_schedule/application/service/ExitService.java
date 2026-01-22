package solvit.teachmon.domain.student_schedule.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.student_schedule.application.mapper.ExitMapper;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.domain.repository.ExitRepository;
import solvit.teachmon.domain.student_schedule.exception.ExitNotFoundException;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ExitHistoryResponse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExitService {
    private final ExitRepository exitRepository;
    private final ExitMapper exitMapper;

    public List<ExitHistoryResponse> getWeekExitHistory() {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.FRIDAY);

        List<ExitEntity> exits = exitRepository.findAllByDateRange(startOfWeek, endOfWeek);

        return exits.stream()
                .map(exitMapper::toExitHistoryResponse)
                .toList();
    }

    public List<ExitHistoryResponse> getExitHistoryByDay(LocalDate day) {
        List<ExitEntity> exits = exitRepository.findAllByDay(day);

        return exits.stream()
                .map(exitMapper::toExitHistoryResponse)
                .toList();
    }

    @Transactional
    public void deleteExit(Long exitId) {
        ExitEntity exit = exitRepository.findById(exitId)
                .orElseThrow(ExitNotFoundException::new);

        exitRepository.delete(exit);
    }
}
