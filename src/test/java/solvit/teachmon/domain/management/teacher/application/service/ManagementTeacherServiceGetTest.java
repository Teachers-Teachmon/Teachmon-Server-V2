package solvit.teachmon.domain.management.teacher.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 관리 서비스 - 전체 선생님 조회 테스트")
class ManagementTeacherServiceGetTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SupervisionScheduleRepository supervisionScheduleRepository;

    @InjectMocks
    private ManagementTeacherService managementTeacherService;

    private List<TeacherListResponse> mockTeacherList;

    @BeforeEach
    void setUp() {
        mockTeacherList = Arrays.asList(
                new TeacherListResponse(1L, Role.TEACHER, "김선생", "kim@teacher.com", 5),
                new TeacherListResponse(2L, Role.TEACHER, "이선생", "lee@teacher.com", 3),
                new TeacherListResponse(3L, Role.TEACHER, "박선생", "park@teacher.com", 7)
        );
    }

    @Test
    @DisplayName("모든 선생님 목록과 감독 횟수를 조회할 수 있다")
    void shouldGetAllTeachersWithSupervisionCount() {
        // Given: 선생님 목록과 각 선생님의 감독 횟수가 있을 때
        given(supervisionScheduleRepository.countTeacherSupervision()).willReturn(mockTeacherList);

        // When: 모든 선생님 목록을 조회하면
        List<TeacherListResponse> result = managementTeacherService.getAllTeachers();

        // Then: 선생님 목록과 감독 횟수가 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).name()).isEqualTo("김선생");
        assertThat(result.get(0).supervisionCount()).isEqualTo(5);
        assertThat(result.get(1).name()).isEqualTo("이선생");
        assertThat(result.get(1).supervisionCount()).isEqualTo(3);
        assertThat(result.get(2).name()).isEqualTo("박선생");
        assertThat(result.get(2).supervisionCount()).isEqualTo(7);

        // 리포지토리 메서드가 한 번 호출되었는지 검증
        verify(supervisionScheduleRepository, times(1)).countTeacherSupervision();
    }

    @Test
    @DisplayName("선생님이 없을 때 빈 목록을 반환한다")
    void shouldReturnEmptyListWhenNoTeachersExist() {
        // Given: 선생님이 없을 때
        given(supervisionScheduleRepository.countTeacherSupervision()).willReturn(List.of());

        // When: 모든 선생님 목록을 조회하면
        List<TeacherListResponse> result = managementTeacherService.getAllTeachers();

        // Then: 빈 목록이 반환된다
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // 리포지토리 메서드가 한 번 호출되었는지 검증
        verify(supervisionScheduleRepository, times(1)).countTeacherSupervision();
    }

    @Test
    @DisplayName("감독 횟수가 0인 선생님도 조회할 수 있다")
    void shouldGetTeachersWithZeroSupervisionCount() {
        // Given: 감독 횟수가 0인 선생님이 포함된 목록이 있을 때
        List<TeacherListResponse> teachersWithZeroCount = Arrays.asList(
                new TeacherListResponse(1L, Role.TEACHER, "김선생", "kim@teacher.com", 0),
                new TeacherListResponse(2L, Role.TEACHER, "이선생", "lee@teacher.com", 5)
        );
        given(supervisionScheduleRepository.countTeacherSupervision()).willReturn(teachersWithZeroCount);

        // When: 모든 선생님 목록을 조회하면
        List<TeacherListResponse> result = managementTeacherService.getAllTeachers();

        // Then: 감독 횟수가 0인 선생님도 포함되어 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).supervisionCount()).isEqualTo(0);
        assertThat(result.get(1).supervisionCount()).isEqualTo(5);
    }
}
