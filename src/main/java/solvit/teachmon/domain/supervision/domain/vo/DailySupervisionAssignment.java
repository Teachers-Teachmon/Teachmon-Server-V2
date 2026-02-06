package solvit.teachmon.domain.supervision.domain.vo;

import lombok.Builder;

/**
 * 하루 감독 배정 결과를 담는 값 객체
 */
@Builder
public record DailySupervisionAssignment(
        TeacherSupervisionInfo selfStudyTeacher,    // 자습 감독 교사
        TeacherSupervisionInfo leaveSeatTeacher     // 이석 감독 교사
) {
}