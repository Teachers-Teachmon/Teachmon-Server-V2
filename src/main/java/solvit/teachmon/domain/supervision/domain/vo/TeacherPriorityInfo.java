package solvit.teachmon.domain.supervision.domain.vo;

/**
 * 교사의 우선순위 정보를 담는 값 객체
 */
public record TeacherPriorityInfo(
        TeacherSupervisionInfo teacherInfo,
        double priority  // 높을수록 우선순위 높음
) {
}