package solvit.teachmon.domain.student_schedule.application.batch.tasklet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.leave_seat.domain.entity.LeaveSeatEntity;
import solvit.teachmon.domain.leave_seat.domain.repository.LeaveSeatRepository;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.student_schedule.application.batch.dto.StudentScheduleInfo;
import solvit.teachmon.domain.student_schedule.application.batch.stepscope.StudentScheduleInfoMapHolder;
import solvit.teachmon.domain.student_schedule.application.service.StudentScheduleGenerator;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.global.enums.WeekDay;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Step 0: StudentSchedule 빈 틀 생성 및 사전 데이터 로딩
 *
 * 1. 기존 미래 스케줄 삭제 → 새 빈 틀 생성
 * 2. stackOrderMap: StudentSchedule별 최대 stackOrder AtomicInteger
 * 3. businessTripSet: "afterSchoolId_day" → 주간 출장 Set
 * 4. occupiedPlaceSet: "day_period_placeId" → 이번 주 점유된 place Set
 *    (AfterSchool + LeaveSeat + AfterSchoolReinforcement 기준)
 *    → Processor에서 checkPlaceAvailability N+1 완전 제거
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StudentScheduleGeneratorTasklet implements Tasklet {
    private final StudentScheduleGenerator studentScheduleGenerator;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentScheduleRepository studentScheduleRepository;
    private final AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;
    private final AfterSchoolRepository afterSchoolRepository;
    private final AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;
    private final LeaveSeatRepository leaveSeatRepository;
    private final BranchRepository branchRepository;
    private final StudentScheduleInfoMapHolder studentScheduleInfoMapHolder;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        LocalDate baseDate = (LocalDate) chunkContext.getStepContext()
                .getJobParameters()
                .get("baseDate");

        log.info("Step 0: Creating empty StudentScheduleEntity for baseDate={}", baseDate);

        // 1. 기존 미래 스케줄 삭제 및 새 빈 틀 생성
        List<StudentEntity> students = getNowStudents(baseDate);
        studentScheduleGenerator.deleteFutureStudentSchedules(baseDate);
        studentScheduleGenerator.createStudentScheduleByStudents(students, baseDate);

        // 2. stackOrderMap: StudentSchedule별 현재 최대 stackOrder (Step 0 초기화 후 항상 0)
        int expectedSize = students.size() * 15;
        int initialCapacity = (int) (expectedSize / 0.75) + 1;
        ConcurrentHashMap<Long, AtomicInteger> stackOrderMap = new ConcurrentHashMap<>(initialCapacity);
        for (ScheduleRepository.MaxStackOrderProjection row : scheduleRepository.findMaxStackOrderGroupByStudentScheduleId()) {
            stackOrderMap.put(row.getStudentScheduleId(), new AtomicInteger(row.getMaxStackOrder()));
        }

        LocalDate weekStart = baseDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = baseDate.with(DayOfWeek.SUNDAY);

        // 3. businessTripSet: "afterSchoolId_day" → 주간 출장 Set
        List<AfterSchoolBusinessTripEntity> weeklyBusinessTrips =
                afterSchoolBusinessTripRepository.findAllByDayBetween(weekStart, weekEnd);
        Set<String> businessTripSet = new HashSet<>();
        for (AfterSchoolBusinessTripEntity trip : weeklyBusinessTrips) {
            businessTripSet.add(trip.getAfterSchool().getId() + "_" + trip.getDay());
        }

        // 4. occupiedPlaceSet: "day_period_placeId" → 이번 주 사용 중인 place Set
        //    checkPlaceAvailability()의 3가지 쿼리를 단일 사전로딩으로 대체
        Set<String> occupiedPlaceSet = buildOccupiedPlaceSet(baseDate, weekStart, weekEnd, businessTripSet);

        // 5. studentScheduleMap: "grade_day_period" → List<StudentScheduleInfo>
        //    step4(AfterSchool), step5(Reinforcement), step1 Processor의 findAllByDayBetween 중복 조회 제거
        Map<String, List<StudentScheduleInfo>> studentScheduleMap = buildStudentScheduleMap(weekStart, weekEnd);
        studentScheduleInfoMapHolder.init(studentScheduleMap);

        // 6. ExecutionContext에 적재
        var executionContext = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext();
        executionContext.put("stackOrderMap", stackOrderMap);
        executionContext.put("businessTripSet", businessTripSet);
        executionContext.put("occupiedPlaceSet", occupiedPlaceSet);
        // studentScheduleMap은 step 범위에서 직접 조회하므로 ExecutionContext 미저장 (크기 과대 방지)

        log.info("Step 0: Created {} student schedules, {} stackOrder entries, {} business trips, {} occupied places, {} schedule map entries",
                students.size(), stackOrderMap.size(), businessTripSet.size(), occupiedPlaceSet.size(), studentScheduleMap.size());

        return RepeatStatus.FINISHED;
    }

    /**
     * 이번 주에 점유된 place를 "day_period_placeId" 형태의 Set으로 구성.
     * checkPlaceAvailability() 내부의 3가지 DB 조회를 한 번에 처리.
     *
     * 점유 기준:
     * 1. AfterSchool: 해당 요일+교시에 방과후가 있고 출장이 아닌 경우
     * 2. LeaveSeat: 해당 날짜+교시에 이석이 있는 경우
     * 3. AfterSchoolReinforcement: 해당 날짜+교시에 보강이 있는 경우
     */
    private Set<String> buildOccupiedPlaceSet(
            LocalDate baseDate,
            LocalDate weekStart,
            LocalDate weekEnd,
            Set<String> businessTripSet
    ) {
        Set<String> occupied = new HashSet<>();

        BranchEntity branch = branchRepository.findByDay(baseDate)
                .orElseThrow(BranchNotFoundException::new);

        // (1) AfterSchool place 점유 (place fetch join → lazy 로딩 N+1 방지)
        List<AfterSchoolEntity> afterSchools = afterSchoolRepository.findAllByBranchWithPlace(branch);
        for (AfterSchoolEntity afterSchool : afterSchools) {
            if (afterSchool.getIsEnd() || afterSchool.getPlace() == null) continue;
            // 이번 주 해당 요일 날짜 계산
            LocalDate afterSchoolDay = baseDate.with(afterSchool.getWeekDay().toDayOfWeek());
            if (afterSchoolDay.isBefore(weekStart) || afterSchoolDay.isAfter(weekEnd)) continue;
            // 출장이면 점유 아님
            if (businessTripSet.contains(afterSchool.getId() + "_" + afterSchoolDay)) continue;
            occupied.add(afterSchoolDay + "_" + afterSchool.getPeriod() + "_" + afterSchool.getPlace().getId());
        }

        // (2) LeaveSeat place 점유
        List<LeaveSeatEntity> leaveSeats = leaveSeatRepository.findAllByDayBetween(weekStart, weekEnd);
        for (LeaveSeatEntity leaveSeat : leaveSeats) {
            occupied.add(leaveSeat.getDay() + "_" + leaveSeat.getPeriod() + "_" + leaveSeat.getPlace().getId());
        }

        // (3) AfterSchoolReinforcement place 점유
        List<AfterSchoolReinforcementEntity> reinforcements =
                afterSchoolReinforcementRepository.findAllByChangeDayBetween(weekStart, weekEnd);
        for (AfterSchoolReinforcementEntity r : reinforcements) {
            if (r.getPlace() == null) continue;
            occupied.add(r.getChangeDay() + "_" + r.getChangePeriod() + "_" + r.getPlace().getId());
        }

        return occupied;
    }

    private Map<String, List<StudentScheduleInfo>> buildStudentScheduleMap(LocalDate weekStart, LocalDate weekEnd) {
        List<StudentScheduleInfo> infos = studentScheduleRepository.findAllInfoByDayBetween(weekStart, weekEnd);
        Map<String, List<StudentScheduleInfo>> map = new HashMap<>(infos.size() * 2);
        for (StudentScheduleInfo info : infos) {
            String key = info.grade() + "_" + info.day() + "_" + info.period();
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(info);
        }
        return map;
    }

    private List<StudentEntity> getNowStudents(LocalDate baseDate) {
        Integer nowYear = baseDate.getYear();
        return studentRepository.findByYear(nowYear);
    }
}
