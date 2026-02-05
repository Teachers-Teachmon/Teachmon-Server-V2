package solvit.teachmon.domain.student_schedule.application.strategy;

import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.exception.IllegalStudentStateChangeException;
import solvit.teachmon.global.exception.TeachmonSystemError;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity.ALLOWED_CHANGE_TYPES;

@Component
public class StudentScheduleChangeStrategyComposite {
    private final Map<ScheduleType, StudentScheduleChangeStrategy> studentScheduleStrategyMap;

    @PostConstruct
    public void validateStrategies() {
        // 구현되지 않은 학생 스케줄 변경 전략이 있는지 확인
        Set<ScheduleType> missingChangeStrategy = ALLOWED_CHANGE_TYPES.stream()
                .filter(type -> !studentScheduleStrategyMap.containsKey(type))
                .collect(toSet());

        if (!missingChangeStrategy.isEmpty()) {
            throw new TeachmonSystemError(
                    "구현되지 않은 학생 스케줄 변경 전략이 존재합니다",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public StudentScheduleChangeStrategyComposite(Set<StudentScheduleChangeStrategy> strategies) {
        this.studentScheduleStrategyMap = strategies.stream()
                .collect(toMap(StudentScheduleChangeStrategy::getScheduleType, identity()));
    }

    public StudentScheduleChangeStrategy getStrategy(ScheduleType scheduleType) {
        return Optional.ofNullable(studentScheduleStrategyMap.get(scheduleType))
                .orElseThrow(IllegalStudentStateChangeException::new);
    }
}
