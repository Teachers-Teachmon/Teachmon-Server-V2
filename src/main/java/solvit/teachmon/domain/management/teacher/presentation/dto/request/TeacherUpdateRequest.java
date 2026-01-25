package solvit.teachmon.domain.management.teacher.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import solvit.teachmon.domain.user.domain.enums.Role;

public record TeacherUpdateRequest(
        @NotNull(message = "선생님 정보 수정에서 role(권한)은 필수 입니다")
        Role role,

        @NotBlank(message = "선생님 정보 수정에서 name(이름)은 필수 입니다")
        String name
) {}
