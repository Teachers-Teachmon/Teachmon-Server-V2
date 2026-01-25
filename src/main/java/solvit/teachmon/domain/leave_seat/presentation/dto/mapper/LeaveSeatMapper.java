package solvit.teachmon.domain.leave_seat.presentation.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatStudentEntity;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.LeaveSeatDetailResponse;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.LeaveSeatListResponse;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.StudentInfoResponse;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface LeaveSeatMapper {

    @Mapping(target = "leaveSeatId", source = "leaveSeat.id")
    @Mapping(target = "period", source = "leaveSeat.period")
    @Mapping(target = "teacher", source = "leaveSeat.teacher.name")
    @Mapping(target = "place", source = "leaveSeat.place.name")
    @Mapping(target = "personnel", expression = "java(leaveSeatStudents.size())")
    @Mapping(target = "students", source = "leaveSeatStudents", qualifiedByName = "mapStudentNames")
    LeaveSeatListResponse toListResponse(
            LeaveSeatEntity leaveSeat,
            List<LeaveSeatStudentEntity> leaveSeatStudents
    );

    @Mapping(target = "day", source = "leaveSeat.day")
    @Mapping(target = "teacher", source = "leaveSeat.teacher.name")
    @Mapping(target = "period", source = "leaveSeat.period")
    @Mapping(target = "place", source = "leaveSeat.place.id")
    @Mapping(target = "cause", source = "leaveSeat.cause")
    @Mapping(target = "students", expression = "java(mapStudentInfosWithScheduleTypes(students, studentLastScheduleTypes))")
    LeaveSeatDetailResponse toDetailResponse(
            LeaveSeatEntity leaveSeat,
            List<StudentEntity> students,
            Map<Long, ScheduleType> studentLastScheduleTypes
    );

    @Named("mapStudentNames")
    default List<String> mapStudentNames(List<LeaveSeatStudentEntity> leaveSeatStudents) {
        return leaveSeatStudents.stream()
                .map(ls -> ls.getStudent().calculateStudentNumber() + ls.getStudent().getName())
                .toList();
    }

    default List<StudentInfoResponse> mapStudentInfosWithScheduleTypes(
            List<StudentEntity> students,
            Map<Long, ScheduleType> studentLastScheduleTypes
    ) {
        return students.stream()
                .map(student -> {
                    ScheduleType scheduleType = studentLastScheduleTypes.get(student.getId());
                    String state = scheduleType.name();

                    return StudentInfoResponse.builder()
                            .number(student.calculateStudentNumber())
                            .name(student.getName())
                            .state(state)
                            .build();
                })
                .toList();
    }
}
