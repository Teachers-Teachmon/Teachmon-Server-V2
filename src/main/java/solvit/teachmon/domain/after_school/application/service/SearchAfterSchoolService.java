package solvit.teachmon.domain.after_school.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolSearchResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchAfterSchoolService {
    private final AfterSchoolRepository afterSchoolRepository;

    public List<AfterSchoolSearchResponseDto> searchAfterSchoolByQuery(String query) {
        return afterSchoolRepository.searchAfterSchoolsByKeyword(query);
    }
}