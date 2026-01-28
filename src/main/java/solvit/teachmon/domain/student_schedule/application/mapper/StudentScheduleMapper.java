package solvit.teachmon.domain.student_schedule.application.mapper;

import org.mapstruct.Mapper;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.student_schedule.application.dto.PeriodScheduleDto;
import solvit.teachmon.domain.student_schedule.application.dto.StudentScheduleDto;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.ClassStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.HistoryStudentScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.PeriodScheduleResponse;
import solvit.teachmon.domain.student_schedule.presentation.dto.response.StudentScheduleResponse;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StudentScheduleMapper {
    default ClassStudentScheduleResponse toResponse(Integer classNumber, List<StudentScheduleDto> studentSchedulesDtos) {
        return ClassStudentScheduleResponse.builder()
                    // 반 설정
                    .classNumber(classNumber)
                    .students(studentSchedulesDtos.stream()
                            // 반 학생들 스케줄 설정
                            .map(dto -> StudentScheduleResponse.builder()
                                    .studentId(dto.studentId())
                                    .number(dto.number())
                                    .name(dto.name())
                                    .state(dto.state())
                                    .scheduleId(dto.scheduleId())
                                    .build()
                            )
                            .toList()
                    )
                    .build();
    }

    StudentScheduleResponse toStudentScheduleResponse(StudentScheduleDto dto);

    default HistoryStudentScheduleResponse toHistoryResponse(StudentEntity student, List<PeriodScheduleDto> scheduleDtos) {

        // 기존 states 맵 생성
        Map<SchoolPeriod, PeriodScheduleResponse> scheduleTypes = scheduleDtos.stream()
                .filter(dto -> dto.period() != null)
                .collect(Collectors.toMap(
                        PeriodScheduleDto::period,
                        dto -> PeriodScheduleResponse.builder()
                                .scheduleId(dto.scheduleId())
                                .state(dto.type())
                                .build()
                ));

        return HistoryStudentScheduleResponse.builder()
                .studentNumber(student.calculateStudentNumber())
                .name(student.getName())
                .onePeriod(scheduleTypes.getOrDefault(SchoolPeriod.ONE_PERIOD, null))
                .twoPeriod(scheduleTypes.getOrDefault(SchoolPeriod.TWO_PERIOD, null))
                .threePeriod(scheduleTypes.getOrDefault(SchoolPeriod.THREE_PERIOD, null))
                .fourPeriod(scheduleTypes.getOrDefault(SchoolPeriod.FOUR_PERIOD, null))
                .fivePeriod(scheduleTypes.getOrDefault(SchoolPeriod.FIVE_PERIOD, null))
                .sixPeriod(scheduleTypes.getOrDefault(SchoolPeriod.SIX_PERIOD, null))
                .sevenPeriod(scheduleTypes.getOrDefault(SchoolPeriod.SEVEN_PERIOD, null))
                .eightAndNinePeriod(scheduleTypes.getOrDefault(SchoolPeriod.EIGHT_AND_NINE_PERIOD, null))
                .tenAndElevenPeriod(scheduleTypes.getOrDefault(SchoolPeriod.TEN_AND_ELEVEN_PERIOD, null))
                .build();
    }
}
