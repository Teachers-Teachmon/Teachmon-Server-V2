package solvit.teachmon.domain.supervision.domain.strategy;

import org.springframework.stereotype.Component;
import solvit.teachmon.domain.supervision.domain.dto.TeacherSupervisionInfo;

import java.time.LocalDate;

/**
 * 기본 감독 우선순위 계산 전략
 * 공식: Priority Score = !banday * lastSupervisionDays / (10 * supervisionCount)
 */
@Component
public class DefaultPriorityStrategy implements SupervisionPriorityStrategy {

    private static final double SUPERVISION_COUNT_WEIGHT = 10.0;

    /**
     * 우선순위 점수 계산
     * - banday가 1이면 해당 요일 금지 → 우선순위 0
     * - banday가 0이면 허용 → 공식 적용
     */
    @Override
    public double calculatePriority(TeacherSupervisionInfo teacherInfo, LocalDate targetDate) {
        // SupervisionBanDayRepository에서 조회한 금지요일 확인
        boolean isBanDay = teacherInfo.isBanDay(targetDate.getDayOfWeek());
        int bandayValue = isBanDay ? 1 : 0;  // 금지요일이면 1, 아니면 0
        int notBandayValue = 1 - bandayValue; // !banday: 금지요일이면 0, 아니면 1

        if (notBandayValue == 0) {
            return 0.0; // 금지 요일이면 우선순위 0 (배정 불가)
        }

        // 최근 감독일로부터 경과일 (높을수록 우선순위 증가)
        long daysSinceLastSupervision = teacherInfo.getDaysSinceLastSupervision(targetDate);
        
        // 총 감독 횟수 (0으로 나누기 방지)
        int totalSupervisionCount = Math.max(1, teacherInfo.totalSupervisionCount());

        // Priority Score = !banday * lastSupervisionDays / (10 * supervisionCount)
        return (double) (notBandayValue * daysSinceLastSupervision) / (SUPERVISION_COUNT_WEIGHT * totalSupervisionCount);
    }
}