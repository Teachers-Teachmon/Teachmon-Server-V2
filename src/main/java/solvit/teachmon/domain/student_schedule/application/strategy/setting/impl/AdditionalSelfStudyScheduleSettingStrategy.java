package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.AdditionalSelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.exception.NoAvailablePlaceException;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AdditionalSelfStudyScheduleRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static solvit.teachmon.domain.place.domain.entity.PlaceEntity.calculateNextClassNumber;

@Component
@RequiredArgsConstructor
public class AdditionalSelfStudyScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final AdditionalSelfStudyRepository additionalSelfStudyRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final AdditionalSelfStudyScheduleRepository additionalSelfStudyScheduleRepository;
    private final PlaceRepository placeRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.ADDITIONAL_SELF_STUDY;
    }

    @Override
    public void settingSchedule() {
        List<AdditionalSelfStudyEntity> additionalSelfStudies = findWeeklyAdditionalSelfStudies();

        for(AdditionalSelfStudyEntity additionalSelfStudy : additionalSelfStudies) {
            List<StudentScheduleEntity> studentSchedules = findStudentScheduleByAdditionalSelfStudy(additionalSelfStudy);
            settingAdditionalSelfStudySchedule(studentSchedules, additionalSelfStudy);
        }
    }

    private List<AdditionalSelfStudyEntity> findWeeklyAdditionalSelfStudies() {
        LocalDate today = LocalDate.now();

        LocalDate startDay = today.with(DayOfWeek.MONDAY).plusWeeks(1);
        LocalDate endDay = today.with(DayOfWeek.SUNDAY).plusWeeks(1);

        return additionalSelfStudyRepository.findAllByDayBetween(startDay, endDay);
    }

    private List<StudentScheduleEntity> findStudentScheduleByAdditionalSelfStudy(AdditionalSelfStudyEntity additionalSelfStudy) {
        return studentScheduleRepository.findAllByGradeAndDayAndPeriod(
                additionalSelfStudy.getGrade(), additionalSelfStudy.getDay(), additionalSelfStudy.getPeriod()
        );
    }

    private void settingAdditionalSelfStudySchedule(
            List<StudentScheduleEntity> studentSchedules,
            AdditionalSelfStudyEntity additionalSelfStudy
    ) {
        for(StudentScheduleEntity studentSchedule : studentSchedules) {
            ScheduleEntity newSchedule = createNewSchedule(studentSchedule);
            createAdditionalSelfStudySchedule(newSchedule, additionalSelfStudy, findAdditionalSelfStudyPlace(studentSchedule));
        }
    }

    private PlaceEntity findAdditionalSelfStudyPlace(StudentScheduleEntity studentSchedule) {
        StudentEntity student = studentSchedule.getStudent();
        Map<Integer, PlaceEntity> placesMap = placeRepository.findAllByGradePrefix(student.getGrade());

        Integer targetPoint = student.getClassNumber();
        for(int count = 0; count < 4; count++) {
            PlaceEntity place = placesMap.get(targetPoint);

            if(!placeRepository.existByDayAndPeriodAndPlace(
                    studentSchedule.getDay(), studentSchedule.getPeriod(), place
            ))
                return place;
            targetPoint = calculateNextClassNumber(targetPoint);
        }

        throw new NoAvailablePlaceException();
    }

    private void createAdditionalSelfStudySchedule(ScheduleEntity schedule, AdditionalSelfStudyEntity additionalSelfStudy, PlaceEntity place) {
        // 자습 스케줄 생성
        AdditionalSelfStudyScheduleEntity additionalSelfStudySchedule = AdditionalSelfStudyScheduleEntity.builder()
                .schedule(schedule)
                .place(place)
                .additionalSelfStudy(additionalSelfStudy)
                .build();

        additionalSelfStudyScheduleRepository.save(additionalSelfStudySchedule);
    }

    private ScheduleEntity createNewSchedule(StudentScheduleEntity studentSchedule) {
        // 새로운 스케줄 생성
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(studentSchedule, lastStackOrder, ScheduleType.ADDITIONAL_SELF_STUDY);

        scheduleRepository.save(newSchedule);

        return newSchedule;
    }
}
