package solvit.teachmon.domain.after_school.application.service;

import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.Map;

public record ReferenceDataCache(
    Map<String, TeacherEntity> teacherEntityMap,
    Map<String, PlaceEntity> placeEntityMap,
    Map<String, StudentEntity> studentEntityMap
) {

    public boolean hasTeacher(String email) {
        return teacherEntityMap.containsKey(email);
    }

    public boolean hasPlace(String name) {
        return placeEntityMap.containsKey(name);
    }

    public boolean hasStudent(Integer number, String name) {
        return studentEntityMap.containsKey(number + "_" + name);
    }

    public TeacherEntity getTeacher(String email) {
        return teacherEntityMap.get(email);
    }

    public PlaceEntity getPlace(String name) {
        return placeEntityMap.get(name);
    }

    public StudentEntity getStudent(Integer number, String name) {
        return this.studentEntityMap.get(number + "_" + name);
    }
}
