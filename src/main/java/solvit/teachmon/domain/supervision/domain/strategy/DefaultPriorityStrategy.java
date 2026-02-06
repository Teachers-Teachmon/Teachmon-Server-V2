package solvit.teachmon.domain.supervision.domain.strategy;

import org.springframework.stereotype.Component;
import solvit.teachmon.domain.supervision.domain.vo.TeacherSupervisionCalculator;
import solvit.teachmon.domain.supervision.domain.vo.TeacherSupervisionInfo;

import java.time.LocalDate;

/**
 * 기본 감독 우선순위 계산 전략
 * 공식: Priority Score = !banday * lastSupervisionDays / (10 * supervisionCount)
 */
@Component
public class DefaultPriorityStrategy implements SupervisionPriorityStrategy {

    private static final double SUPERVISION_COUNT_WEIGHT = 10.0;

    @Override
    public double calculatePriority(TeacherSupervisionInfo teacherInfo, LocalDate targetDate) {
        TeacherSupervisionCalculator calculator = new TeacherSupervisionCalculator(teacherInfo);
        
        if (calculator.isBanDay(targetDate.getDayOfWeek())) {
            return 0.0;
        }

        long daysSinceLastSupervision = calculator.getDaysSinceLastSupervision(targetDate);
        int totalSupervisionCount = Math.max(1, teacherInfo.totalSupervisionCount());

        return (double) daysSinceLastSupervision / (SUPERVISION_COUNT_WEIGHT * totalSupervisionCount);
    }
}