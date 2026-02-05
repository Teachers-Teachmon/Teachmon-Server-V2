package solvit.teachmon.domain.student_schedule.application.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.place.PlaceNotFoundException;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.student_schedule.application.dto.PlaceScheduleDto;
import solvit.teachmon.domain.student_schedule.application.mapper.PlaceStudentScheduleMapper;
import solvit.teachmon.domain.student_schedule.application.mapper.StudentScheduleMapper;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.enums.ScheduleType;
import solvit.teachmon.domain.student_schedule.domain.repository.StudentScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AdditionalSelfStudyScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.AfterSchoolScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.LeaveSeatScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.SelfStudyScheduleRepository;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.FloorStateResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PlaceStateResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PlaceStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.StudentScheduleResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PlaceStudentScheduleService {
    private final StudentScheduleRepository studentScheduleRepository;
    private final SelfStudyScheduleRepository selfStudyScheduleRepository;
    private final AdditionalSelfStudyScheduleRepository additionalSelfStudyScheduleRepository;
    private final LeaveSeatScheduleRepository leaveSeatScheduleRepository;
    private final AfterSchoolScheduleRepository afterSchoolScheduleRepository;
    private final PlaceRepository placeRepository;
    private final PlaceStudentScheduleMapper placeStudentScheduleMapper;
    private final StudentScheduleMapper studentScheduleMapper;
    private final List<ScheduleType> placeScheduleType = List.of(
            ScheduleType.SELF_STUDY,
            ScheduleType.ADDITIONAL_SELF_STUDY,
            ScheduleType.LEAVE_SEAT,
            ScheduleType.AFTER_SCHOOL
    );

    @Transactional(readOnly = true)
    public List<FloorStateResponse> getAllFloorsPlaceCount(LocalDate day, SchoolPeriod period) {
        // 해당 시간의 장소를 사용하고 있는 스케줄 조회
        Map<ScheduleType, List<ScheduleEntity>> placeFillScheduleMap = studentScheduleRepository.findAllByDayAndPeriodAndTypeIn(day, period, placeScheduleType);

        // 각 스케줄 타입별로 층별 장소 사용 인원 조회
        Map<Integer, Long> selfStudyCountMap = selfStudyScheduleRepository.getSelfStudyPlaceCount(placeFillScheduleMap.get(ScheduleType.SELF_STUDY));
        Map<Integer, Long> additionalSelfStudyCountMap = additionalSelfStudyScheduleRepository.getAdditionalSelfStudyPlaceCount(placeFillScheduleMap.get(ScheduleType.ADDITIONAL_SELF_STUDY));
        Map<Integer, Long> leaveSeatCountMap = leaveSeatScheduleRepository.getLeaveSeatPlaceCount(placeFillScheduleMap.get(ScheduleType.LEAVE_SEAT));
        Map<Integer, Long> afterSchoolCountMap = afterSchoolScheduleRepository.getAfterSchoolPlaceCount(placeFillScheduleMap.get(ScheduleType.AFTER_SCHOOL));

        // 각 층별로 장소 사용 인원 합산
        Map<Integer, Long> result = Stream.of(
                        selfStudyCountMap,
                        additionalSelfStudyCountMap,
                        leaveSeatCountMap,
                        afterSchoolCountMap
                ).flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Long::sum
                ));


        return placeStudentScheduleMapper.toFloorStateResponses(result);
    }

    @Transactional(readOnly = true)
    public List<PlaceStateResponse> getPlaceStatesByFloor(Integer floor, LocalDate day, SchoolPeriod period) {
        // 해당 시간의 장소를 사용하고 있는 스케줄 조회
        Map<ScheduleType, List<ScheduleEntity>> placeFillScheduleMap = studentScheduleRepository.findAllByDayAndPeriodAndTypeIn(day, period, placeScheduleType);

        // 각 스케줄별 장소 추출
        List<PlaceScheduleDto> placeSchedules = Stream.of(
                selfStudyScheduleRepository.getPlaceScheduleByFloor(placeFillScheduleMap.get(ScheduleType.SELF_STUDY), floor),
                additionalSelfStudyScheduleRepository.getPlaceScheduleByFloor(placeFillScheduleMap.get(ScheduleType.ADDITIONAL_SELF_STUDY), floor),
                leaveSeatScheduleRepository.getPlaceScheduleByFloor(placeFillScheduleMap.get(ScheduleType.LEAVE_SEAT), floor),
                afterSchoolScheduleRepository.getPlaceScheduleByFloor(placeFillScheduleMap.get(ScheduleType.AFTER_SCHOOL), floor)
        ).flatMap(List::stream)
                .toList();

        return placeStudentScheduleMapper.toPlaceStateResponses(placeSchedules);
    }

    @Transactional(readOnly = true)
    public PlaceStudentScheduleResponse getStudentsByPlaceId(Long placeId, LocalDate day, SchoolPeriod period) {
        PlaceEntity place = placeRepository.findById(placeId)
                .orElseThrow(PlaceNotFoundException::new);

        // 해당 장소의 학생 스케줄 조회
        List<StudentScheduleResponse> studentScheduleResponses = Stream.of(
                selfStudyScheduleRepository.getStudentScheduleByPlaceAndDayAndPeriod(placeId, day, period),
                additionalSelfStudyScheduleRepository.getStudentScheduleByPlaceAndDayAndPeriod(placeId, day, period),
                leaveSeatScheduleRepository.getStudentScheduleByPlaceAndDayAndPeriod(placeId, day, period),
                afterSchoolScheduleRepository.getStudentScheduleByPlaceAndDayAndPeriod(placeId, day, period)
        ).flatMap(List::stream)
                .map(studentScheduleMapper::toStudentScheduleResponse)
                .toList();

        return placeStudentScheduleMapper.toPlaceStudentScheduleResponse(place, studentScheduleResponses);
    }
}
