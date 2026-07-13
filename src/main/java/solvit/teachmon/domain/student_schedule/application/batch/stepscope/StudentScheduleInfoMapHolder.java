package solvit.teachmon.domain.student_schedule.application.batch.stepscope;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.student_schedule.application.batch.dto.StudentScheduleInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StudentScheduleInfoMapHolder {
    private final Map<String, List<StudentScheduleInfo>> EMPTY_MAP = Map.of();
    private Map<String, List<StudentScheduleInfo>> cachedMap;

    public synchronized void init(Map<String, List<StudentScheduleInfo>> map) {
        this.cachedMap = new HashMap<>(map);
    }

    public synchronized Map<String, List<StudentScheduleInfo>> get() {
        return cachedMap != null ? cachedMap : EMPTY_MAP;
    }

    public synchronized void clear() {
        cachedMap = null;
    }
}