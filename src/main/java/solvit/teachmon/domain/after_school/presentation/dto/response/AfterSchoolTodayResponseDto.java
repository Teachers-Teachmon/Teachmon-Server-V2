package solvit.teachmon.domain.after_school.presentation.dto.response;

public record AfterSchoolTodayResponseDto(
        Long id,
        Integer branch,
        String name,
        PlaceInfo place,
        Integer grade,
        String period,
        String day
) {
    public record PlaceInfo(
            Long id,
            String name
    ) {
    }
}
