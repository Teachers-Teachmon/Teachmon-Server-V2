package solvit.teachmon.domain.student_schedule.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
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

    default List<PlaceStateResponse> toPlaceStateResponses(List<PlaceScheduleDto> dtos) {
        return dtos.stream()
                .map(dto -> PlaceStateResponse.builder()
                        .placeId(dto.place().getId())
                        .placeName(dto.place().getName())
                        .state(dto.scheduleType())
                        .build())
                .toList();
    }

    default PlaceStudentScheduleResponse toPlaceStudentScheduleResponses(PlaceEntity place, List<StudentScheduleResponse> studentSchedules) {
        return PlaceStudentScheduleResponse.builder()
                .placeId(place.getId())
                .placeName(place.getName())
                .students(studentSchedules)
                .build();
    }
}
