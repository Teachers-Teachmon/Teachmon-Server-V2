package solvit.teachmon.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 학교 교시를 나타내는 열거형
 * Enumeration representing school periods
 * 
 * <p>중학교 방과 후 자습 시간대를 정의합니다:
 * Defines after-school self-study time periods for middle school:
 * <ul>
 *   <li>7교시: 정규 수업 후 첫 자습 시간 (7th period: First self-study period after regular classes)</li>
 *   <li>8~9교시: 중간 자습 시간 (두 교시 묶음) (8-9th periods: Mid self-study block, grouped as two periods)</li>
 *   <li>10~11교시: 후반 자습 시간 (두 교시 묶음) (10-11th periods: Late self-study block, grouped as two periods)</li>
 * </ul>
 * 
 * <p>교시 그룹화 이유: 8~9교시와 10~11교시는 일반적으로 함께 운영되므로 묶어서 관리합니다.
 * Periods are grouped because 8-9 and 10-11 periods are typically managed together as blocks.
 * 
 * @see solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity
 */
@Getter
@RequiredArgsConstructor
public enum SchoolPeriod {
    /** 7교시 (7th period) */
    SEVEN_PERIOD("7교시"),
    /** 8~9교시 (8th and 9th periods combined) */
    EIGHT_AND_NINE_PERIOD("8~9교시"),
    /** 10~11교시 (10th and 11th periods combined) */
    TEN_AND_ELEVEN_PERIOD("10~11교시");

    /** 교시 표시명 (한글) (Period display name in Korean) */
    private final String period;
}
