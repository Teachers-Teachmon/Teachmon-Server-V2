package solvit.teachmon.domain.self_study.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdditionalSelfStudyMapper {
    default List<AdditionalSelfStudyEntity> toEntities(AdditionalSelfStudySetRequest request) {
        return request.periods().stream()
                .map(period -> AdditionalSelfStudyEntity.builder()
                        .day(request.day())
                        .period(period)
                        .grade(request.grade())
                        .build())
                .toList();

    }
}
