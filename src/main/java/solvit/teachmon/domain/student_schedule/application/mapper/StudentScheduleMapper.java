package solvit.teachmon.domain.student_schedule.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.StudentScheduleResponse;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface StudentScheduleMapper {
    default List<ClassStudentScheduleResponse> toResponse(Map<Integer, List<StudentScheduleDto>> classStudentSchedule) {
        return classStudentSchedule.entrySet().stream()
                .map(entry -> ClassStudentScheduleResponse.builder()
                        // 반 설정
                        .classNumber(entry.getKey())
                        .students(entry.getValue().stream()
                                // 반 학생들 스케줄 설정
                                .map(dto -> StudentScheduleResponse.builder()
                                        .studentId(dto.studentId())
                                        .number(dto.number())
                                        .name(dto.name())
                                        .state(dto.state())
                                        .scheduleId(dto.scheduleId())
                                        .build()
                                )
                                .toList()
                        )
                        .build()
                )
                .toList();
    }
}
