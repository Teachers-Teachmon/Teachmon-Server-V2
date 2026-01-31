package solvit.teachmon.domain.after_school.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.querydsl.core.annotations.QueryProjection;

public record AfterSchoolResponseDto(
        Long id,
        @JsonProperty("week_day")
        String weekDay,
        String period,
        String name,
        TeacherInfo teacher,
        PlaceInfo place
) {
    public record TeacherInfo(
            Long id,
            String name
    ) {
        @QueryProjection
        public TeacherInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public record PlaceInfo(
            Long id,
            String name
    ) {
        @QueryProjection
        public PlaceInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @QueryProjection
    public AfterSchoolResponseDto(Long id, String weekDay, String period, String name, TeacherInfo teacher, PlaceInfo place) {
        this.id = id;
        this.weekDay = weekDay;
        this.period = period;
        this.name = name;
        this.teacher = teacher;
        this.place = place;
    }
}
