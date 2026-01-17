package solvit.teachmon.domain.self_study.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.presentation.dto.common.WeekDaySelfStudyDto;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface SelfStudyMapper {
    default List<SelfStudyEntity> toEntities(List<WeekDaySelfStudyDto> dtos, BranchEntity branch, Integer grade) {
        return dtos.stream()
                .flatMap(dto -> dto.periods().stream()
                        .distinct()
                        .map(p -> SelfStudyEntity.builder()
                                .branch(branch)
                                .grade(grade)
                                .weekDay(dto.weekDay())
                                .period(p)
                                .build()))
                .toList();
    }

    default List<WeekDaySelfStudyDto> toWeekDaySelfStudyDtos(
            Map<WeekDay, List<SchoolPeriod>> map
    ) {
        return Arrays.stream(WeekDay.values())
                .map(weekDay -> new WeekDaySelfStudyDto(
                        weekDay,
                        map.getOrDefault(weekDay, Collections.emptyList())
                ))
                .toList();
    }
}
