package solvit.teachmon.domain.management.teacher.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import solvit.teachmon.domain.user.domain.enums.Role;

@QueryProjection
@Builder
public record TeacherListResponse(
        @JsonProperty("teacher_id")
        Long teacherId,

        Role role,

        String name,

        String email,

        @JsonProperty("supervision_count")
        Integer supervisionCount
) {}
