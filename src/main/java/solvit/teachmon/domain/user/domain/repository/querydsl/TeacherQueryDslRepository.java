package solvit.teachmon.domain.user.domain.repository.querydsl;

import org.springframework.stereotype.Repository;
import solvit.teachmon.domain.user.presentation.dto.response.TeacherProfileResponseDto;

import java.util.Optional;

@Repository
public interface TeacherQueryDslRepository {
    Optional<TeacherProfileResponseDto> findUserProfileById(Long id);
}
