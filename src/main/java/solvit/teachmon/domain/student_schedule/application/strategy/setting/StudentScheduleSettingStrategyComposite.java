package solvit.teachmon.domain.student_schedule.application.strategy.setting;

import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.exception.IllegalStudentStateSettingException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Component
public class StudentScheduleSettingStrategyComposite {
    private final Map<ScheduleType, StudentScheduleSettingStrategy> studentScheduleStrategyMap;

    public StudentScheduleSettingStrategyComposite(Set<StudentScheduleSettingStrategy> strategies) {
        this.studentScheduleStrategyMap = strategies.stream()
                .collect(toMap(StudentScheduleSettingStrategy::getScheduleType, identity()));
    }

    public StudentScheduleSettingStrategy getStrategy(ScheduleType scheduleType) {
        return Optional.ofNullable(studentScheduleStrategyMap.get(scheduleType))
                .orElseThrow(IllegalStudentStateSettingException::new);
    }

    public List<StudentScheduleSettingStrategy> getAllStrategies() {
        return List.copyOf(studentScheduleStrategyMap.values());
    }
}
