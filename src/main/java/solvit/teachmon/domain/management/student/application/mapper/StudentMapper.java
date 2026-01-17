package solvit.teachmon.domain.management.student.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.presentation.dto.request.StudentRequest;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    StudentEntity toEntity(StudentRequest studentRequest);
}
