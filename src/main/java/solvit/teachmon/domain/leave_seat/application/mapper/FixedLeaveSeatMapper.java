package solvit.teachmon.domain.leave_seat.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.FixedLeaveSeatStudentEntity;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.FixedLeaveSeatDetailResponse;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.FixedLeaveSeatListResponse;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.FixedLeaveSeatStudentInfoResponse;
import solvit.teachmon.domain.leave_seat.presentation.dto.response.StudentDetailInfoResponse;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FixedLeaveSeatMapper {

    @Mapping(target = "staticLeaveSeatId", source = "fixedLeaveSeat.id")
    @Mapping(target = "weekDay", source = "fixedLeaveSeat.weekDay")
    @Mapping(target = "period", source = "fixedLeaveSeat.period")
    @Mapping(target = "place", source = "fixedLeaveSeat.place.name")
    @Mapping(target = "personnel", expression = "java(fixedLeaveSeatStudents.size())")
    @Mapping(target = "students", source = "fixedLeaveSeatStudents", qualifiedByName = "mapStudentInfos")
    FixedLeaveSeatListResponse toListResponse(
            FixedLeaveSeatEntity fixedLeaveSeat,
            List<FixedLeaveSeatStudentEntity> fixedLeaveSeatStudents
    );

    @Mapping(target = "weekDay", source = "fixedLeaveSeat.weekDay")
    @Mapping(target = "period", source = "fixedLeaveSeat.period")
    @Mapping(target = "place", expression = "java(new PlaceInfoResponse(fixedLeaveSeat.getPlace().getId(), fixedLeaveSeat.getPlace().getName()))")
    @Mapping(target = "cause", source = "fixedLeaveSeat.cause")
    @Mapping(target = "students", source = "students", qualifiedByName = "mapStudentDetailInfos")
    FixedLeaveSeatDetailResponse toDetailResponse(
            FixedLeaveSeatEntity fixedLeaveSeat,
            List<StudentEntity> students
    );

    @Named("mapStudentInfos")
    default List<FixedLeaveSeatStudentInfoResponse> mapStudentInfos(List<FixedLeaveSeatStudentEntity> fixedLeaveSeatStudents) {
        return fixedLeaveSeatStudents.stream()
                .map(fls ->
                        FixedLeaveSeatStudentInfoResponse.builder()
                                .number(fls.getStudent().calculateStudentNumber())
                                .name(fls.getStudent().getName())
                                .build()
                )
                .toList();
    }

    @Named("mapStudentDetailInfos")
    default List<StudentDetailInfoResponse> mapStudentDetailInfos(List<StudentEntity> students) {
        return students.stream()
                .map(student ->
                        StudentDetailInfoResponse.builder()
                                .id(student.getId())
                                .number(student.calculateStudentNumber())
                                .name(student.getName())
                                .build()
                )
                .toList();
    }
}
