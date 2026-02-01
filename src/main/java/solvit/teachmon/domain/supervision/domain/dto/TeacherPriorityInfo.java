package solvit.teachmon.domain.supervision.domain.dto;

/**
 * 교사의 우선순위 정보를 담는 DTO
 */
public record TeacherPriorityInfo(
        TeacherSupervisionInfo teacherInfo,
        double priority  // 높을수록 우선순위 높음
) {
}