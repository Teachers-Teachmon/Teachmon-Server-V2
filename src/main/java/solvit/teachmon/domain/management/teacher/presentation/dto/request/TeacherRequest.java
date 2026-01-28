package solvit.teachmon.domain.management.teacher.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import solvit.teachmon.domain.user.domain.enums.Role;

public record TeacherRequest(
        @NotNull(message = "선생님 추가에서 role(권한)은 필수입니다.")
        Role role,

        @NotBlank(message = "선생님 추가에서 name(이름)은 필수입니다.")
        String name,

        @NotBlank(message = "선생님 추가에서 email(이메일)은 필수입니다.")
        @Email(message = "선생님 추가에서 email(이메일) 형식이 올바르지 않습니다.")
        String email
) {}
