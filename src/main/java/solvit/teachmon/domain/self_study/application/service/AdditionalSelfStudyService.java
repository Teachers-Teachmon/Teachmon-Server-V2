package solvit.teachmon.domain.self_study.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.application.mapper.AdditionalSelfStudyMapper;
import solvit.teachmon.domain.self_study.domain.entity.AdditionalSelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.AdditionalSelfStudyRepository;
import solvit.teachmon.domain.self_study.presentation.dto.request.AdditionalSelfStudySetRequest;
import solvit.teachmon.domain.self_study.presentation.dto.response.AdditionalSelfStudyGetResponse;
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

@Service
@RequiredArgsConstructor
public class AdditionalSelfStudyService {
    private final AdditionalSelfStudyRepository additionalSelfStudyRepository;
    private final AdditionalSelfStudyMapper additionalSelfStudyMapper;
    private final StudentScheduleRepository studentScheduleRepository;
    private final ScheduleRepository scheduleRepository;
    private final AdditionalSelfStudyScheduleRepository additionalSelfStudyScheduleRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public void setAdditionalSelfStudy(AdditionalSelfStudySetRequest request) {
        List<AdditionalSelfStudyEntity> additionalSelfStudyEntities = additionalSelfStudyMapper.toEntities(request);

        additionalSelfStudyRepository.saveAll(additionalSelfStudyEntities);

        // 요청한 날짜와 추가 자습 날짜가 같은 주에 있는지 확인
        if (isInCurrentWeek(request.day())) {
            // Apply schedules immediately for the current week
            applyAdditionalSelfStudySchedules(additionalSelfStudyEntities);
        }
    }

    @Transactional(readOnly = true)
    public List<AdditionalSelfStudyGetResponse> getAdditionalSelfStudy(Integer year) {
        return additionalSelfStudyRepository.findGroupedByDayAndGrade(year);
    }

    @Transactional
    public void deleteAdditionalSelfStudy(Long additionalId) {
        AdditionalSelfStudyEntity additionalSelfStudy = additionalSelfStudyRepository.findById(additionalId)
                .orElseThrow(() -> new IllegalArgumentException("Additional self-study not found"));

        // Check if the date is in the current week
        if (isInCurrentWeek(additionalSelfStudy.getDay())) {
            // Remove schedules immediately for the current week
            removeAdditionalSelfStudySchedules(additionalSelfStudy);
        }

        additionalSelfStudyRepository.deleteById(additionalId);
    }

    private boolean isInCurrentWeek(LocalDate date) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY);

        return !date.isBefore(startOfWeek) && !date.isAfter(endOfWeek);
    }

    private void applyAdditionalSelfStudySchedules(List<AdditionalSelfStudyEntity> additionalSelfStudies) {
        for (AdditionalSelfStudyEntity additionalSelfStudy : additionalSelfStudies) {
            List<StudentScheduleEntity> studentSchedules = studentScheduleRepository.findAllByGradeAndDayAndPeriod(
                    additionalSelfStudy.getGrade(),
                    additionalSelfStudy.getDay(),
                    additionalSelfStudy.getPeriod()
            );

            for (StudentScheduleEntity studentSchedule : studentSchedules) {
                ScheduleEntity newSchedule = createNewSchedule(studentSchedule);
                PlaceEntity place = findAdditionalSelfStudyPlace(studentSchedule);
                createAdditionalSelfStudySchedule(newSchedule, additionalSelfStudy, place);
            }
        }
    }

    private void removeAdditionalSelfStudySchedules(AdditionalSelfStudyEntity additionalSelfStudy) {
        List<StudentScheduleEntity> studentSchedules = studentScheduleRepository.findAllByGradeAndDayAndPeriod(
                additionalSelfStudy.getGrade(),
                additionalSelfStudy.getDay(),
                additionalSelfStudy.getPeriod()
        );

        for (StudentScheduleEntity studentSchedule : studentSchedules) {
            scheduleRepository.deleteByStudentScheduleIdAndType(
                    studentSchedule.getId(),
                    ScheduleType.ADDITIONAL_SELF_STUDY
            );
        }
    }

    private ScheduleEntity createNewSchedule(StudentScheduleEntity studentSchedule) {
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(
                studentSchedule,
                lastStackOrder,
                ScheduleType.ADDITIONAL_SELF_STUDY
        );

        return scheduleRepository.save(newSchedule);
    }

    private PlaceEntity findAdditionalSelfStudyPlace(StudentScheduleEntity studentSchedule) {
        StudentEntity student = studentSchedule.getStudent();
        Map<Integer, PlaceEntity> placesMap = placeRepository.findAllByGradePrefix(student.getGrade());

        Integer targetPoint = student.getClassNumber();
        for (int count = 0; count < 4; count++) {
            PlaceEntity place = placesMap.get(targetPoint);

            if (!placeRepository.existByDayAndPeriodAndPlace(
                    studentSchedule.getDay(), studentSchedule.getPeriod(), place
            ))
                return place;
            targetPoint = calculateNextClassNumber(targetPoint);
        }

        throw new NoAvailablePlaceException();
    }

    private void createAdditionalSelfStudySchedule(
            ScheduleEntity schedule,
            AdditionalSelfStudyEntity additionalSelfStudy,
            PlaceEntity place
    ) {
        AdditionalSelfStudyScheduleEntity additionalSelfStudySchedule = AdditionalSelfStudyScheduleEntity.builder()
                .schedule(schedule)
                .place(place)
                .additionalSelfStudy(additionalSelfStudy)
                .build();

        additionalSelfStudyScheduleRepository.save(additionalSelfStudySchedule);
    }
}
