package solvit.teachmon.domain.student_schedule.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.FloorStateResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PlaceStateResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PlaceStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.StudentScheduleResponse;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface PlaceStudentScheduleMapper {
    default List<FloorStateResponse> toFloorStateResponses(Map<Integer, Long> floorStateMap) {
        return floorStateMap.entrySet().stream()
                .map(entry -> FloorStateResponse.builder()
                        .floor(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
    }

    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "state", source = "scheduleType")
    PlaceStateResponse toPlaceStateResponse(
            PlaceScheduleDto dto
    );

    List<PlaceStateResponse> toPlaceStateResponses(
            List<PlaceScheduleDto> dtos
    );

    @Mapping(target = "placeId", source = "place.id")
    @Mapping(target = "placeName", source = "place.name")
    @Mapping(target = "students", source = "studentSchedules")
    PlaceStudentScheduleResponse toPlaceStudentScheduleResponse(
            PlaceEntity place,
            List<StudentScheduleResponse> studentSchedules
    );
}
