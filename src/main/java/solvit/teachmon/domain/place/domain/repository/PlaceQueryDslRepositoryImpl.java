package solvit.teachmon.domain.place.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.after_school.domain.entity.QAfterSchoolEntity;
import solvit.teachmon.domain.leave_seat.domain.entity.QLeaveSeatEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.entity.QPlaceEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlaceQueryDslRepositoryImpl implements PlaceQueryDslRepository{
    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Integer, PlaceEntity> findAllByGradePrefix(Integer grade) {
        QPlaceEntity place = QPlaceEntity.placeEntity;

        return queryFactory
                .selectFrom(place)
                .where(place.name.startsWith(grade + "-"))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        p -> extractClassNumber(p.getName()),
                        Function.identity()
                ));
    }

    @Override
    public Boolean existByDayAndPeriodAndPlace(LocalDate day, SchoolPeriod period, PlaceEntity place) {
        Boolean afterSchoolExist = existAfterSchoolPlaceByDayAndPeriodAndPlace(day, period, place);
        Boolean leaveSeatExist = existLeaveSeatPlaceByDayAndPeriodAndPlace(day, period, place);

        return afterSchoolExist || leaveSeatExist;
    }

    private Boolean existAfterSchoolPlaceByDayAndPeriodAndPlace(LocalDate day, SchoolPeriod period, PlaceEntity place) {
        QAfterSchoolEntity afterSchool = QAfterSchoolEntity.afterSchoolEntity;
        WeekDay weekDay = WeekDay.fromLocalDate(day);

        return queryFactory
                .selectOne()
                .from(afterSchool)
                .where(
                        afterSchool.weekDay.eq(weekDay),
                        afterSchool.period.eq(period),
                        afterSchool.place.eq(place)
                )
                .fetchFirst() != null;
    }

    private Boolean existLeaveSeatPlaceByDayAndPeriodAndPlace(LocalDate day, SchoolPeriod period, PlaceEntity place) {
        QLeaveSeatEntity leaveSeat = QLeaveSeatEntity.leaveSeatEntity;

         return queryFactory
                 .selectOne()
                 .from(leaveSeat)
                 .where(
                         leaveSeat.day.eq(day),
                         leaveSeat.period.eq(period),
                         leaveSeat.place.eq(place)
                 )
                 .fetchFirst() != null;
    }

    private Integer extractClassNumber(String name) {
        // 반 추출하기
        return Integer.parseInt(name.substring(name.indexOf('-') + 1));
    }

}
