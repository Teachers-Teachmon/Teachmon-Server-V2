package solvit.teachmon.domain.management.teacher.application.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherUpdateRequest;
import solvit.teachmon.domain.supervision.domain.repository.SupervisionScheduleRepository;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.domain.user.exception.TeacherNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 관리 서비스 - 선생님 정보 수정 테스트")
class ManagementTeacherFacadeServiceUpdateTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SupervisionScheduleRepository supervisionScheduleRepository;

    @InjectMocks
    private ManagementTeacherFacadeService managementTeacherFacadeService;

    @Test
    @DisplayName("선생님 정보를 업데이트할 수 있다")
    void shouldUpdateTeacher() {
        // Given: 선생님이 존재하고, 업데이트할 정보가 있을 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        TeacherUpdateRequest updateRequest = new TeacherUpdateRequest(Role.ADMIN, "김관리자");

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: 선생님 정보를 업데이트하면
        managementTeacherFacadeService.updateTeacher(updateRequest, teacherId);

        // Then: 선생님의 역할과 이름이 변경된다
        assertThat(teacher.getRole()).isEqualTo(Role.ADMIN);
        assertThat(teacher.getName()).isEqualTo("김관리자");
        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    @DisplayName("존재하지 않는 선생님 ID로 업데이트하면 예외가 발생한다")
    void shouldThrowExceptionWhenTeacherNotFoundOnUpdate() {
        // Given: 존재하지 않는 선생님 ID가 있을 때
        Long teacherId = 999L;
        TeacherUpdateRequest updateRequest = new TeacherUpdateRequest(Role.ADMIN, "김관리자");

        given(teacherRepository.findById(teacherId)).willReturn(Optional.empty());

        // When & Then: 업데이트하면 예외가 발생한다
        assertThatThrownBy(() -> managementTeacherFacadeService.updateTeacher(updateRequest, teacherId))
                .isInstanceOf(TeacherNotFoundException.class);

        verify(teacherRepository, times(1)).findById(teacherId);
    }

    @Test
    @DisplayName("선생님의 역할만 변경할 수 있다")
    void shouldUpdateTeacherRole() {
        // Given: 선생님이 존재하고, 역할만 변경하려고 할 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        TeacherUpdateRequest updateRequest = new TeacherUpdateRequest(Role.ADMIN, "김선생");

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: 역할을 업데이트하면
        managementTeacherFacadeService.updateTeacher(updateRequest, teacherId);

        // Then: 역할만 변경되고 이름은 유지된다
        assertThat(teacher.getRole()).isEqualTo(Role.ADMIN);
        assertThat(teacher.getName()).isEqualTo("김선생");
    }

    @Test
    @DisplayName("선생님의 이름만 변경할 수 있다")
    void shouldUpdateTeacherName() {
        // Given: 선생님이 존재하고, 이름만 변경하려고 할 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        TeacherUpdateRequest updateRequest = new TeacherUpdateRequest(Role.TEACHER, "김교사");

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: 이름을 업데이트하면
        managementTeacherFacadeService.updateTeacher(updateRequest, teacherId);

        // Then: 이름만 변경되고 역할은 유지된다
        assertThat(teacher.getName()).isEqualTo("김교사");
        assertThat(teacher.getRole()).isEqualTo(Role.TEACHER);
    }

    @Test
    @DisplayName("TEACHER에서 GUEST로 역할을 변경할 수 있다")
    void shouldChangeRoleFromTeacherToGuest() {
        // Given: TEACHER 역할의 선생님이 있을 때
        Long teacherId = 1L;
        TeacherEntity teacher = TeacherEntity.builder()
                .name("김선생")
                .mail("kim@teacher.com")
                .profile("https://profile.url/image.png")
                .build();
        TeacherUpdateRequest updateRequest = new TeacherUpdateRequest(Role.GUEST, "김선생");

        given(teacherRepository.findById(teacherId)).willReturn(Optional.of(teacher));

        // When: GUEST로 역할을 변경하면
        managementTeacherFacadeService.updateTeacher(updateRequest, teacherId);

        // Then: 역할이 GUEST로 변경된다
        assertThat(teacher.getRole()).isEqualTo(Role.GUEST);
        assertThat(teacher.getName()).isEqualTo("김선생");
    }
}
