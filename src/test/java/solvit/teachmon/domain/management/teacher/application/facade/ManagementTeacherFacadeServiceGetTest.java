package solvit.teachmon.domain.management.teacher.application.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.management.teacher.presentation.dto.response.TeacherListResponse;
import solvit.teachmon.domain.supervision.application.dto.TeacherSupervisionCountDto;
import solvit.teachmon.domain.supervision.application.service.SupervisionService;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 관리 서비스 - 전체 선생님 조회 테스트")
class ManagementTeacherFacadeServiceGetTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SupervisionService supervisionService;

    @Mock
    private SupervisionBanDayRepository supervisionBanDayRepository;

    @InjectMocks
    private ManagementTeacherFacadeService managementTeacherFacadeService;

    private List<TeacherSupervisionCountDto> mockTeacherDtoList;

    @BeforeEach
    void setUp() {
        mockTeacherDtoList = Arrays.asList(
                new TeacherSupervisionCountDto(1L, Role.TEACHER, "김선생", "kim@teacher.com", "김선생 프로필", 5),
                new TeacherSupervisionCountDto(2L, Role.TEACHER, "이선생", "lee@teacher.com", "이선생 프로필", 3),
                new TeacherSupervisionCountDto(3L, Role.TEACHER, "박선생", "park@teacher.com", "박선생 프로필", 7)
        );
    }

    @Test
    @DisplayName("모든 선생님 목록과 감독 횟수를 조회할 수 있다")
    void shouldGetAllTeachersWithSupervisionCount() {
        // Given: 선생님 목록과 각 선생님의 감독 횟수가 있을 때
        given(supervisionService.searchTeacherWithSupervisionCounts(null)).willReturn(mockTeacherDtoList);

        // When: 모든 선생님 목록을 조회하면
        List<TeacherListResponse> result = managementTeacherFacadeService.getAllTeachers(null);

        // Then: 선생님 목록과 감독 횟수가 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).name()).isEqualTo("김선생");
        assertThat(result.get(0).supervisionCount()).isEqualTo(5);
        assertThat(result.get(1).name()).isEqualTo("이선생");
        assertThat(result.get(1).supervisionCount()).isEqualTo(3);
        assertThat(result.get(2).name()).isEqualTo("박선생");
        assertThat(result.get(2).supervisionCount()).isEqualTo(7);

        // 서비스 메서드가 한 번 호출되었는지 검증
        verify(supervisionService, times(1)).searchTeacherWithSupervisionCounts(null);
    }

    @Test
    @DisplayName("선생님이 없을 때 빈 목록을 반환한다")
    void shouldReturnEmptyListWhenNoTeachersExist() {
        // Given: 선생님이 없을 때
        given(supervisionService.searchTeacherWithSupervisionCounts(null)).willReturn(List.of());

        // When: 모든 선생님 목록을 조회하면
        List<TeacherListResponse> result = managementTeacherFacadeService.getAllTeachers(null);

        // Then: 빈 목록이 반환된다
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // 서비스 메서드가 한 번 호출되었는지 검증
        verify(supervisionService, times(1)).searchTeacherWithSupervisionCounts(null);
    }

    @Test
    @DisplayName("감독 횟수가 0인 선생님도 조회할 수 있다")
    void shouldGetTeachersWithZeroSupervisionCount() {
        // Given: 감독 횟수가 0인 선생님이 포함된 목록이 있을 때
        List<TeacherSupervisionCountDto> teachersWithZeroCount = Arrays.asList(
                new TeacherSupervisionCountDto(1L, Role.TEACHER, "김선생", "kim@teacher.com", "김선생 프로필", 0),
                new TeacherSupervisionCountDto(2L, Role.TEACHER, "이선생", "lee@teacher.com", "이선생 프로필", 5)
        );
        given(supervisionService.searchTeacherWithSupervisionCounts(null)).willReturn(teachersWithZeroCount);

        // When: 모든 선생님 목록을 조회하면
        List<TeacherListResponse> result = managementTeacherFacadeService.getAllTeachers(null);

        // Then: 감독 횟수가 0인 선생님도 포함되어 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).supervisionCount()).isEqualTo(0);
        assertThat(result.get(1).supervisionCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("이름으로 선생님을 검색할 수 있다")
    void shouldSearchTeachersByName() {
        // Given: "김"으로 검색했을 때 "김선생"만 반환되는 상황
        String query = "김";
        List<TeacherSupervisionCountDto> filteredList = List.of(
                new TeacherSupervisionCountDto(1L, Role.TEACHER, "김선생", "kim@teacher.com", "김선생 프로필", 5)
        );
        given(supervisionService.searchTeacherWithSupervisionCounts(query)).willReturn(filteredList);

        // When: 이름으로 검색하면
        List<TeacherListResponse> result = managementTeacherFacadeService.getAllTeachers(query);

        // Then: 검색어가 포함된 선생님만 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("김선생");
        assertThat(result.getFirst().supervisionCount()).isEqualTo(5);

        // 서비스 메서드가 올바른 query와 함께 호출되었는지 검증
        verify(supervisionService, times(1)).searchTeacherWithSupervisionCounts(query);
    }

    @Test
    @DisplayName("빈 문자열로 검색하면 모든 선생님이 조회된다")
    void shouldGetAllTeachersWhenQueryIsEmpty() {
        // Given: 빈 문자열로 검색했을 때 모든 선생님이 반환되는 상황
        String query = "";
        given(supervisionService.searchTeacherWithSupervisionCounts(query)).willReturn(mockTeacherDtoList);

        // When: 빈 문자열로 검색하면
        List<TeacherListResponse> result = managementTeacherFacadeService.getAllTeachers(query);

        // Then: 모든 선생님이 반환된다
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        // 서비스 메서드가 빈 문자열과 함께 호출되었는지 검증
        verify(supervisionService, times(1)).searchTeacherWithSupervisionCounts(query);
    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
    void shouldReturnEmptyListWhenNoMatchingTeachers() {
        // Given: 검색어에 해당하는 선생님이 없을 때
        String query = "존재하지않는이름";
        given(supervisionService.searchTeacherWithSupervisionCounts(query)).willReturn(List.of());

        // When: 검색하면
        List<TeacherListResponse> result = managementTeacherFacadeService.getAllTeachers(query);

        // Then: 빈 목록이 반환된다
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        // 서비스 메서드가 query와 함께 호출되었는지 검증
        verify(supervisionService, times(1)).searchTeacherWithSupervisionCounts(query);
    }
}
