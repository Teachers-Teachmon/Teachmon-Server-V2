package solvit.teachmon.domain.student_schedule.application.batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.batch.dto.SelfStudyScheduleDto;
import solvit.teachmon.domain.student_schedule.domain.entity.ScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.entity.schedules.SelfStudyScheduleEntity;
import solvit.teachmon.domain.student_schedule.domain.repository.ScheduleRepository;
import solvit.teachmon.domain.student_schedule.domain.repository.schedules.SelfStudyScheduleRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelfStudyScheduleWriter implements ItemWriter<List<SelfStudyScheduleDto>> {
    private final ScheduleRepository scheduleRepository;
    private final SelfStudyScheduleRepository selfStudyScheduleRepository;

    @Override
    public void write(Chunk<? extends List<SelfStudyScheduleDto>> chunk) {
        List<SelfStudyScheduleDto> allItems = new ArrayList<>();
        chunk.getItems().forEach(allItems::addAll);

        List<ScheduleEntity> schedules = new ArrayList<>(allItems.size());
        List<SelfStudyScheduleEntity> selfStudySchedules = new ArrayList<>(allItems.size());

        allItems.forEach(dto -> {
            schedules.add(dto.schedule());
            selfStudySchedules.add(dto.selfStudySchedule());
        });

        scheduleRepository.saveAll(schedules);
        selfStudyScheduleRepository.saveAll(selfStudySchedules);

        log.info("SelfStudyScheduleWriter saved {} items", allItems.size());
    }
}
