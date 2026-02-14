package solvit.teachmon.domain.after_school.domain.repository.querydsl;

import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolSearchRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolResponseDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolMyResponseDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolSearchResponseDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolTodayResponseDto;

import java.util.List;
import java.util.Optional;

public interface AfterSchoolQueryDslRepository {
    Optional<AfterSchoolEntity> findWithAllRelations(Long afterSchoolId);
    List<AfterSchoolResponseDto> findAfterSchoolsByConditions(AfterSchoolSearchRequestDto searchRequest);
    List<AfterSchoolMyResponseDto> findMyAfterSchoolsByTeacherId(Long teacherId, Integer grade);
    List<AfterSchoolTodayResponseDto> findMyTodayAfterSchoolsByTeacherId(Long teacherId);
    List<AfterSchoolSearchResponseDto> searchAfterSchoolsByKeyword(String keyword);
}
