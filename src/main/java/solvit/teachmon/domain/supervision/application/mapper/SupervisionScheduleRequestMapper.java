package solvit.teachmon.domain.supervision.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionScheduleCreateRequestDto;
import solvit.teachmon.domain.supervision.presentation.dto.request.SupervisionScheduleUpdateRequestDto;

@Mapper(componentModel = "spring")
public interface SupervisionScheduleRequestMapper {
    
    SupervisionScheduleCreateRequestDto toCreateRequest(SupervisionScheduleUpdateRequestDto updateRequest);
}