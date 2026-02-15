package solvit.teachmon.domain.supervision.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionExchangeEntity;
import solvit.teachmon.domain.supervision.domain.entity.SupervisionScheduleEntity;
import solvit.teachmon.domain.supervision.domain.enums.SupervisionType;
import solvit.teachmon.domain.supervision.presentation.dto.response.SupervisionExchangeResponseDto;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

@Mapper(componentModel = "spring")
public interface SupervisionExchangeResponseMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "requestor", source = "senderSchedule")
    @Mapping(target = "responser", source = "recipientSchedule")
    @Mapping(target = "status", source = "state")
    @Mapping(target = "reason", source = "reason")
    SupervisionExchangeResponseDto toResponseDto(SupervisionExchangeEntity exchange);

    @Mapping(target = "teacher", source = "teacher")
    @Mapping(target = "day", source = "day")
    @Mapping(target = "type", source = "type")
    SupervisionExchangeResponseDto.SupervisionInfo toSupervisionInfo(SupervisionScheduleEntity schedule);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    SupervisionExchangeResponseDto.SupervisionInfo.TeacherInfo toTeacherInfo(TeacherEntity teacher);

    default String mapSupervisionType(SupervisionType type) {
        return switch (type) {
            case SELF_STUDY_SUPERVISION -> "self_study";
            case LEAVE_SEAT_SUPERVISION -> "leave_seat";
            case SEVENTH_PERIOD_SUPERVISION -> "seventh_period";
        };
    }
}