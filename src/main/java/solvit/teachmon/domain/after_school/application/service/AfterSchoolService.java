package solvit.teachmon.domain.after_school.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.place.exception.PlaceNotFoundException;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolRequestDto;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.management.student.domain.entity.StudentEntity;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.student.exception.StudentNotFoundException;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;
import solvit.teachmon.global.enums.SchoolPeriod;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AfterSchoolService {
    private final AfterSchoolRepository afterSchoolRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public void createAfterSchool(AfterSchoolRequestDto requestDto) {
        TeacherEntity teacher = getTeacherById(requestDto.teacherId());
        PlaceEntity place = getPlaceById(requestDto.placeId());
        validateStudents(requestDto.studentsId());
        BranchEntity branch = getCurrentBranch();
        
        AfterSchoolEntity afterSchool = createAfterSchoolEntity(requestDto, teacher, branch, place);
        
        afterSchoolRepository.save(afterSchool);
    }

    private TeacherEntity getTeacherById(Long teacherId) {
        return teacherRepository.findById(teacherId)
                .orElseThrow(TeacherNotFoundException::new);
    }

    private PlaceEntity getPlaceById(Long placeId) {
        List<PlaceEntity> places = afterSchoolRepository.findPlacesInBulk(List.of(placeId));
        if (places.isEmpty()) {
            throw new PlaceNotFoundException();
        }
        return places.getFirst();
    }

    private void validateStudents(List<Long> studentIds) {
        List<StudentEntity> students = studentRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            throw new StudentNotFoundException();
        }
    }

    private BranchEntity getCurrentBranch() {
        int currentYear = LocalDate.now().getYear();
        return branchRepository.findByYearOrderByBranch(currentYear).stream()
                .findFirst()
                .orElseThrow(BranchNotFoundException::new);
    }

    private AfterSchoolEntity createAfterSchoolEntity(AfterSchoolRequestDto requestDto, TeacherEntity teacher, 
                                                     BranchEntity branch, PlaceEntity place) {
        return AfterSchoolEntity.builder()
                .teacher(teacher)
                .branch(branch)
                .place(place)
                .weekDay(WeekDay.valueOf(requestDto.weekDay()))
                .period(SchoolPeriod.valueOf(requestDto.period()))
                .name(requestDto.name())
                .grade(requestDto.grade())
                .build();
    }
}