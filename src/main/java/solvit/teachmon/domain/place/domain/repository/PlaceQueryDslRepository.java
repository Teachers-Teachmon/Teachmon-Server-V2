package solvit.teachmon.domain.place.domain.repository;

import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.Map;

public interface PlaceQueryDslRepository {
    Map<Integer, PlaceEntity> findAllByGradePrefix(Integer grade);
    Boolean existByDayAndPeriodAndPlace(LocalDate day, SchoolPeriod period, PlaceEntity place);
}
