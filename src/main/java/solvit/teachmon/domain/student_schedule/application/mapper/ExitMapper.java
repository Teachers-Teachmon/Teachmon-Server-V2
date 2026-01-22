package solvit.teachmon.domain.student_schedule.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.ExitEntity;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ExitHistoryResponse;

@Mapper(componentModel = "spring")
public interface ExitMapper {
    @Mapping(target = "exitId", source = "id")
    @Mapping(target = "day", source = "day")
    @Mapping(target = "teacher", source = "teacher.name")
    @Mapping(target = "number", source = "student", qualifiedByName = "studentToNumber")
    @Mapping(target = "name", source = "student.name")
    @Mapping(target = "period", source = "period")
    ExitHistoryResponse toExitHistoryResponse(ExitEntity exitEntity);

    @Named("studentToNumber")
    default Integer studentToNumber(StudentEntity student) {
        return student.calculateStudentNumber();
    }
}
