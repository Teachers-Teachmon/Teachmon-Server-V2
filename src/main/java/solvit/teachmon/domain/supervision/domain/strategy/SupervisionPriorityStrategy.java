package solvit.teachmon.domain.supervision.domain.strategy;

import solvit.teachmon.domain.supervision.domain.dto.TeacherSupervisionInfo;

import java.time.LocalDate;

/**
 * 감독 우선순위 계산 전략 인터페이스
 */
public interface SupervisionPriorityStrategy {
    
    /**
     * 교사의 감독 배정 우선순위 계산
     * 
     * @param teacherInfo 교사 감독 정보
     * @param targetDate 배정 대상 날짜
     * @return 우선순위 점수 (높을수록 우선순위 높음, 0이면 배정 불가)
     */
    double calculatePriority(TeacherSupervisionInfo teacherInfo, LocalDate targetDate);
}