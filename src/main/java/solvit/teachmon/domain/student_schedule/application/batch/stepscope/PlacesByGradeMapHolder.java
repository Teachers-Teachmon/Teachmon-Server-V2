package solvit.teachmon.domain.student_schedule.application.batch.stepscope;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlacesByGradeMapHolder {
    private final PlaceRepository placeRepository;
    private Map<Integer, Map<Integer, PlaceEntity>> cachedMap;

    public synchronized Map<Integer, Map<Integer, PlaceEntity>> get() {
        if (cachedMap != null) {
            return cachedMap;
        }
        cachedMap = new HashMap<>();
        for (PlaceEntity place : placeRepository.findAll()) {
            String name = place.getName();
            if (!name.contains("-")) continue;
            try {
                int grade = Integer.parseInt(name.substring(0, name.indexOf('-')));
                int classNum = Integer.parseInt(name.substring(name.indexOf('-') + 1));
                cachedMap.computeIfAbsent(grade, k -> new HashMap<>()).put(classNum, place);
            } catch (NumberFormatException ignored) {
            }
        }
        return cachedMap;
    }

    public synchronized void clear() {
        cachedMap = null;
    }
}