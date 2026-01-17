package solvit.teachmon.domain.supervision.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import solvit.teachmon.domain.user.domain.enums.Role;

public record TeacherSupervisionCountDto(
        Long teacherId,
        Role role,
        String name,
        String email,
        Integer supervisionCount
) {
    @QueryProjection
    public TeacherSupervisionCountDto(Long teacherId, Role role, String name, String email, Integer supervisionCount) {
        this.teacherId = teacherId;
        this.role = role;
        this.name = name;
        this.email = email;
        this.supervisionCount = supervisionCount;
    }
}
