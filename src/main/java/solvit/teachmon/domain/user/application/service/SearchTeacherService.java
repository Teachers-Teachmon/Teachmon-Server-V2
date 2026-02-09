package solvit.teachmon.domain.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.presentation.dto.response.TeacherSearchResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchTeacherService {
    private final TeacherRepository teacherRepository;

    public List<TeacherSearchResponseDto> searchTeacherByQuery(String query) {
        return teacherRepository.performTeacherSearch(query);
    }
}