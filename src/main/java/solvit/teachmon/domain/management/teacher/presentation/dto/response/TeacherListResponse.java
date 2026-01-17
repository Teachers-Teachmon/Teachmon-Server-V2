package solvit.teachmon.domain.management.teacher.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import solvit.teachmon.domain.supervision.application.dto.TeacherSupervisionCountDto;
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
) {
        public static TeacherListResponse from(
                TeacherSupervisionCountDto dto
        ) {
                return TeacherListResponse.builder()
                        .teacherId(dto.teacherId())
                        .role(dto.role())
                        .name(dto.name())
                        .email(dto.email())
                        .supervisionCount(dto.supervisionCount())
                        .build();
        }
}
