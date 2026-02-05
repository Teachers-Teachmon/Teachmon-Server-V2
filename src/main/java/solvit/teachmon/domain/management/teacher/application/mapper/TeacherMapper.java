package solvit.teachmon.domain.management.teacher.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherRequest;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
    @Mapping(source = "email", target = "mail")
    @Mapping(target = "profile", ignore = true)
    TeacherEntity toEntity(TeacherRequest teacherRequest);
}
