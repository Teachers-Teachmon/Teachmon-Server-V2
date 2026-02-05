package solvit.teachmon.domain.leave_seat.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(target = "students", source = "fixedLeaveSeatStudents")
    FixedLeaveSeatListResponse toListResponse(
            FixedLeaveSeatEntity fixedLeaveSeat,
            List<FixedLeaveSeatStudentEntity> fixedLeaveSeatStudents
    );

    @Mapping(target = "weekDay", source = "fixedLeaveSeat.weekDay")
    @Mapping(target = "period", source = "fixedLeaveSeat.period")
    @Mapping(target = "place", expression = "java(new PlaceInfoResponse(fixedLeaveSeat.getPlace().getId(), fixedLeaveSeat.getPlace().getName()))")
    @Mapping(target = "cause", source = "fixedLeaveSeat.cause")
    @Mapping(target = "students", source = "students")
    FixedLeaveSeatDetailResponse toDetailResponse(
            FixedLeaveSeatEntity fixedLeaveSeat,
            List<StudentEntity> students
    );

    @Mapping(target = "number",
            expression = "java(entity.getStudent().calculateStudentNumber())")
    @Mapping(target = "name", source = "student.name")
    FixedLeaveSeatStudentInfoResponse toStudentInfo(
            FixedLeaveSeatStudentEntity entity
    );

    @Mapping(target = "number",
            expression = "java(student.calculateStudentNumber())")
    StudentDetailInfoResponse toStudentDetailInfo(
            StudentEntity student
    );

    List<FixedLeaveSeatStudentInfoResponse> toStudentInfos(
            List<FixedLeaveSeatStudentEntity> entities
    );

    List<StudentDetailInfoResponse> toStudentDetailInfos(
            List<StudentEntity> students
    );
}
