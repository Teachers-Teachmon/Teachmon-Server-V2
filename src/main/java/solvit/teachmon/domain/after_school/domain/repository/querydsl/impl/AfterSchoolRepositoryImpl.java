package solvit.teachmon.domain.after_school.domain.repository.querydsl.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.QAfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.querydsl.AfterSchoolQueryDslRepository;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolSearchRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolResponseDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolMyResponseDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolTodayResponseDto;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.entity.QPlaceEntity;
import solvit.teachmon.domain.user.domain.entity.QTeacherEntity;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AfterSchoolRepositoryImpl implements AfterSchoolQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<AfterSchoolEntity> findWithAllRelations(Long afterSchoolId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(QAfterSchoolEntity.afterSchoolEntity)
                        .leftJoin(QAfterSchoolEntity.afterSchoolEntity.teacher, QTeacherEntity.teacherEntity).fetchJoin()
                        .leftJoin(QAfterSchoolEntity.afterSchoolEntity.place, QPlaceEntity.placeEntity).fetchJoin()
                        .leftJoin(QAfterSchoolEntity.afterSchoolEntity.branch).fetchJoin()
                        .where(QAfterSchoolEntity.afterSchoolEntity.id.eq(afterSchoolId))
                        .fetchOne()
        );
    }

    @Override
    public List<PlaceEntity> findPlacesInBulk(List<Long> placeIds) {
        return queryFactory.selectFrom(QPlaceEntity.placeEntity)
                .where(QPlaceEntity.placeEntity.id.in(placeIds))
                .fetch();
    }

    @Override
    public List<AfterSchoolResponseDto> findAfterSchoolsByConditions(AfterSchoolSearchRequestDto searchRequest) {
        QAfterSchoolEntity afterSchool = QAfterSchoolEntity.afterSchoolEntity;
        QTeacherEntity teacher = QTeacherEntity.teacherEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;

        BooleanBuilder whereCondition = new BooleanBuilder();

        whereCondition.and(afterSchool.isEnd.eq(false));

        if (searchRequest.grade() != null) {
            whereCondition.and(afterSchool.grade.eq(searchRequest.grade()));
        }

        if (searchRequest.weekDay() != null) {
            whereCondition.and(afterSchool.weekDay.eq(searchRequest.weekDay()));
        }

        if (searchRequest.startPeriod() != null && searchRequest.endPeriod() != null) {
            SchoolPeriod targetPeriod = mapToSchoolPeriod(searchRequest.startPeriod(), searchRequest.endPeriod());
            if (targetPeriod != null) {
                whereCondition.and(afterSchool.period.eq(targetPeriod));
            }
        }

        List<AfterSchoolEntity> entities = queryFactory
                .selectFrom(afterSchool)
                .join(afterSchool.teacher, teacher).fetchJoin()
                .join(afterSchool.place, place).fetchJoin()
                .where(whereCondition)
                .fetch();

        return entities.stream()
                .map(entity -> new AfterSchoolResponseDto(
                        entity.getId(),
                        mapWeekDayToKorean(entity.getWeekDay()),
                        entity.getPeriod().getPeriod(),
                        entity.getName(),
                        new AfterSchoolResponseDto.TeacherInfo(
                                entity.getTeacher().getId(),
                                entity.getTeacher().getName()
                        ),
                        new AfterSchoolResponseDto.PlaceInfo(
                                entity.getPlace().getId(),
                                entity.getPlace().getName()
                        )
                ))
                .toList();
    }

    private SchoolPeriod mapToSchoolPeriod(Integer startPeriod, Integer endPeriod) {
        if (startPeriod == 7 && endPeriod == 7) {
            return SchoolPeriod.SEVEN_PERIOD;
        } else if (startPeriod == 8 && endPeriod == 9) {
            return SchoolPeriod.EIGHT_AND_NINE_PERIOD;
        } else if (startPeriod == 10 && endPeriod == 11) {
            return SchoolPeriod.TEN_AND_ELEVEN_PERIOD;
        }
        return null;
    }

    private String mapWeekDayToKorean(WeekDay weekDay) {
        return switch (weekDay) {
            case MON -> "월";
            case TUE -> "화";
            case WED -> "수";
            case THU -> "목";
        };
    }

    @Override
    public List<AfterSchoolMyResponseDto> findMyAfterSchoolsByTeacherId(Long teacherId, Integer grade) {
        QAfterSchoolEntity afterSchool = QAfterSchoolEntity.afterSchoolEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;

        BooleanBuilder whereCondition = new BooleanBuilder();
        whereCondition.and(afterSchool.teacher.id.eq(teacherId)).and(afterSchool.isEnd.eq(false));
        
        if (grade != null) {
            whereCondition.and(afterSchool.grade.eq(grade));
        }

        List<AfterSchoolEntity> entities = queryFactory
                .selectFrom(afterSchool)
                .join(afterSchool.place, place).fetchJoin()
                .where(whereCondition)
                .fetch();

        return entities.stream()
                .map(entity -> new AfterSchoolMyResponseDto(
                        entity.getId(),
                        mapWeekDayToKorean(entity.getWeekDay()),
                        entity.getPeriod().getPeriod(),
                        entity.getName(),
                        new AfterSchoolMyResponseDto.PlaceInfo(
                                entity.getPlace().getId(),
                                entity.getPlace().getName()
                        ),
                        0
                ))
                .toList();
    }

    @Override
    public List<AfterSchoolTodayResponseDto> findMyTodayAfterSchoolsByTeacherId(Long teacherId) {
        QAfterSchoolEntity afterSchool = QAfterSchoolEntity.afterSchoolEntity;
        QPlaceEntity place = QPlaceEntity.placeEntity;

        LocalDate today = LocalDate.now();
        WeekDay todayWeekDay = getTodayWeekDay(today);

        List<AfterSchoolEntity> entities = queryFactory
                .selectFrom(afterSchool)
                .join(afterSchool.place, place).fetchJoin()
                .join(afterSchool.branch).fetchJoin()
                .where(afterSchool.teacher.id.eq(teacherId)
                        .and(afterSchool.weekDay.eq(todayWeekDay))
                        .and(afterSchool.isEnd.eq(false)))
                .fetch();

        String todayFormatted = formatTodayDate(today, todayWeekDay);

        return entities.stream()
                .map(entity -> new AfterSchoolTodayResponseDto(
                        entity.getId(),
                        entity.getBranch().getBranch(),
                        entity.getName(),
                        new AfterSchoolTodayResponseDto.PlaceInfo(
                                entity.getPlace().getId(),
                                entity.getPlace().getName()
                        ),
                        entity.getGrade(),
                        entity.getPeriod().getPeriod(),
                        todayFormatted
                ))
                .toList();
    }

    private WeekDay getTodayWeekDay(LocalDate today) {
        return switch (today.getDayOfWeek()) {
            case MONDAY -> WeekDay.MON;
            case TUESDAY -> WeekDay.TUE;
            case WEDNESDAY -> WeekDay.WED;
            case THURSDAY -> WeekDay.THU;
            default -> null;
        };
    }

    private String formatTodayDate(LocalDate today, WeekDay weekDay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREA);
        String dateStr = today.format(formatter);
        String dayName = mapWeekDayToKorean(weekDay) + "요일";
        return dateStr + " " + dayName;
    }
}
