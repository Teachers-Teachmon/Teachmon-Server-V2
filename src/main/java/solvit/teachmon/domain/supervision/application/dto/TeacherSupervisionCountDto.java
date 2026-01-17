package solvit.teachmon.domain.supervision.application.dto;

import com.querydsl.core.annotations.QueryProjection;
import solvit.teachmon.domain.user.domain.enums.Role;

@QueryProjection
public record TeacherSupervisionCountDto(
        Long teacherId,
        Role role,
        String name,
        String email,
        String profile,
        Integer supervisionCount
) {}
