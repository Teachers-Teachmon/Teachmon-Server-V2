package solvit.teachmon.domain.team.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TeamUpdateRequestDto(
        @NotNull(message = "팀 수정 요청에서 id(아이디)는 필수입니다.")
        Long id,
        
        @NotNull(message = "팀 수정 요청에서 name(이름)은 필수입니다.")
        String name,
        
        @NotNull(message = "팀 수정 요청에서 students(학생들)는 필수입니다.")
        List<TeamUpdateStudentDto> students
) {}
