package solvit.teachmon.domain.student_schedule.application.strategy.setting.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.SelfStudyRepository;
import solvit.teachmon.domain.student_schedule.application.strategy.setting.StudentScheduleSettingStrategy;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.StudentScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.SelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.exception.NoAvailablePlaceException;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.SelfStudyScheduleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static solvit.teachmon.domain.place.domain.entity.PlaceEntity.calculateNextClassNumber;

@Component
@RequiredArgsConstructor
public class SelfStudyScheduleSettingStrategy implements StudentScheduleSettingStrategy {
    private final ScheduleRepository scheduleRepository;
    private final SelfStudyRepository selfStudyRepository;
    private final SelfStudyScheduleRepository selfStudyScheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final BranchRepository branchRepository;
    private final PlaceRepository placeRepository;

    @Override
    public ScheduleType getScheduleType() {
        return ScheduleType.SELF_STUDY;
    }

    @Override
    public void settingSchedule() {
        BranchEntity branch = branchRepository.findByDay(LocalDate.now())
                .orElseThrow(BranchNotFoundException::new);

        // 해당 분기의 모든 자습 가져오기
        List<SelfStudyEntity> selfStudies = selfStudyRepository.findAllByBranch(branch);

        for(SelfStudyEntity selfStudy : selfStudies) {
            // 각 자습별 학년들의 student schedule 가져오기
            List<StudentScheduleEntity> studentSchedules = findStudentScheduleBySelfStudy(selfStudy);

            // 각 student schedule 별로 self study 설정해주기
            settingSelfStudySchedule(studentSchedules, selfStudy);
        }
    }

    private List<StudentScheduleEntity> findStudentScheduleBySelfStudy(SelfStudyEntity selfStudy) {
        return studentScheduleRepository.findAllByGradeAndDayAndPeriod(
                selfStudy.getGrade(), calculateSelfStudyDay(selfStudy), selfStudy.getPeriod()
        );
    }

    private LocalDate calculateSelfStudyDay(SelfStudyEntity selfStudy) {
        LocalDate today = LocalDate.now();
        return today.with(selfStudy.getWeekDay().toDayOfWeek()).plusWeeks(1);
    }

    private void settingSelfStudySchedule(List<StudentScheduleEntity> studentSchedules, SelfStudyEntity selfStudy) {
        for(StudentScheduleEntity studentSchedule : studentSchedules) {
            ScheduleEntity newSchedule = createNewSchedule(studentSchedule);
            createSelfStudySchedule(newSchedule, selfStudy, findSelfStudyPlace(studentSchedule));
        }
    }

    private PlaceEntity findSelfStudyPlace(StudentScheduleEntity studentSchedule) {
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

    private void createSelfStudySchedule(ScheduleEntity schedule, SelfStudyEntity selfStudy, PlaceEntity place) {
        // 자습 스케줄 생성
        SelfStudyScheduleEntity selfStudySchedule = SelfStudyScheduleEntity.builder()
                .schedule(schedule)
                .place(place)
                .selfStudy(selfStudy)
                .build();

        selfStudyScheduleRepository.save(selfStudySchedule);
    }

    private ScheduleEntity createNewSchedule(StudentScheduleEntity studentSchedule) {
        // 새로운 스케줄 생성
        Integer lastStackOrder = scheduleRepository.findLastStackOrderByStudentScheduleId(studentSchedule.getId());
        ScheduleEntity newSchedule = ScheduleEntity.createNewStudentSchedule(studentSchedule, lastStackOrder, ScheduleType.SELF_STUDY);

        scheduleRepository.save(newSchedule);

        return newSchedule;
    }
}
