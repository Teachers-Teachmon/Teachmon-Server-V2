package solvit.teachmon.domain.student_schedule.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ExitHistoryResponse;

@Mapper(componentModel = "spring")
public interface ExitMapper {
    @Mapping(target = "exitId", source = "id")
    @Mapping(target = "day", source = "day")
    @Mapping(target = "teacher", source = "teacher.name")
    @Mapping(target = "number",
            expression = "java(exitEntity.getStudent().calculateStudentNumber())")
    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "period", source = "period")
    ExitHistoryResponse toExitHistoryResponse(ExitEntity exitEntity);
}
