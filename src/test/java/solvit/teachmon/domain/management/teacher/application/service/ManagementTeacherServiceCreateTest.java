package solvit.teachmon.domain.management.teacher.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.management.teacher.application.mapper.TeacherMapper;
import solvit.teachmon.domain.management.teacher.exception.TeacherAlreadyExistsException;
import solvit.teachmon.domain.management.teacher.presentation.dto.request.TeacherRequest;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;
import solvit.teachmon.domain.user.domain.enums.Role;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("선생님 관리 서비스 - 선생님 생성 테스트")
class ManagementTeacherServiceCreateTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private ManagementTeacherService managementTeacherService;

    @Captor
    private ArgumentCaptor<TeacherEntity> teacherCaptor;

    @Test
    @DisplayName("선생님을 생성할 수 있다")
    void shouldCreateTeacher() {
        // Given: 선생님 정보가 있을 때
        TeacherRequest request = new TeacherRequest(Role.TEACHER, "김선생", "kim@teacher.com");
        TeacherEntity teacherEntity = TeacherEntity.builder()
                .mail("kim@teacher.com")
                .name("김선생")
                .profile(null)
                .build();
        given(teacherRepository.findByMail(request.email())).willReturn(Optional.empty());
        given(teacherMapper.toEntity(request)).willReturn(teacherEntity);

        // When: 선생님을 생성하면
        managementTeacherService.createTeacher(request);

        // Then: 선생님이 저장된다
        verify(teacherRepository, times(1)).findByMail(request.email());
        verify(teacherRepository, times(1)).save(teacherCaptor.capture());

        TeacherEntity savedTeacher = teacherCaptor.getValue();
        assertThat(savedTeacher.getMail()).isEqualTo("kim@teacher.com");
        assertThat(savedTeacher.getName()).isEqualTo("김선생");
        assertThat(savedTeacher.getRole()).isEqualTo(Role.TEACHER);
    }

    @Test
    @DisplayName("ADMIN 권한으로 선생님을 생성할 수 있다")
    void shouldCreateTeacherWithAdminRole() {
        // Given: ADMIN 권한의 선생님 정보가 있을 때
        TeacherRequest request = new TeacherRequest(Role.ADMIN, "이관리자", "admin@teacher.com");
        TeacherEntity teacherEntity = TeacherEntity.builder()
                .mail("admin@teacher.com")
                .name("이관리자")
                .profile(null)
                .build();
        given(teacherRepository.findByMail(request.email())).willReturn(Optional.empty());
        given(teacherMapper.toEntity(request)).willReturn(teacherEntity);

        // When: 선생님을 생성하면
        managementTeacherService.createTeacher(request);

        // Then: ADMIN 권한으로 선생님이 저장된다
        verify(teacherRepository, times(1)).findByMail(request.email());
        verify(teacherRepository, times(1)).save(teacherCaptor.capture());

        TeacherEntity savedTeacher = teacherCaptor.getValue();
        assertThat(savedTeacher.getMail()).isEqualTo("admin@teacher.com");
        assertThat(savedTeacher.getName()).isEqualTo("이관리자");
        assertThat(savedTeacher.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("이미 등록된 이메일로 선생님 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Given: 이미 등록된 이메일이 있을 때
        TeacherRequest request = new TeacherRequest(Role.TEACHER, "박선생", "existing@teacher.com");
        TeacherEntity existingTeacher = TeacherEntity.builder()
                .mail("existing@teacher.com")
                .name("기존선생")
                .profile(null)
                .build();
        given(teacherRepository.findByMail(request.email())).willReturn(Optional.of(existingTeacher));

        // When & Then: 선생님을 생성하면 예외가 발생한다
        assertThatThrownBy(() -> managementTeacherService.createTeacher(request))
                .isInstanceOf(TeacherAlreadyExistsException.class)
                .hasMessage("이미 등록된 이메일입니다.");

        verify(teacherRepository, times(1)).findByMail(request.email());
        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("다양한 이메일 형식으로 선생님을 생성할 수 있다")
    void shouldCreateTeacherWithVariousEmailFormats() {
        // Given: 다양한 이메일 형식의 선생님 정보가 있을 때
        TeacherRequest request = new TeacherRequest(Role.TEACHER, "최선생", "choi.teacher@bssm.hs.kr");
        TeacherEntity teacherEntity = TeacherEntity.builder()
                .mail("choi.teacher@bssm.hs.kr")
                .name("최선생")
                .profile(null)
                .build();
        given(teacherRepository.findByMail(request.email())).willReturn(Optional.empty());
        given(teacherMapper.toEntity(request)).willReturn(teacherEntity);

        // When: 선생님을 생성하면
        managementTeacherService.createTeacher(request);

        // Then: 해당 이메일로 선생님이 저장된다
        verify(teacherRepository, times(1)).findByMail(request.email());
        verify(teacherRepository, times(1)).save(teacherCaptor.capture());

        TeacherEntity savedTeacher = teacherCaptor.getValue();
        assertThat(savedTeacher.getMail()).isEqualTo("choi.teacher@bssm.hs.kr");
        assertThat(savedTeacher.getName()).isEqualTo("최선생");
    }

    @Test
    @DisplayName("선생님 생성 시 role이 정상적으로 설정된다")
    void shouldSetRoleCorrectlyWhenCreateTeacher() {
        // Given: 선생님 정보가 있을 때
        TeacherRequest request = new TeacherRequest(Role.TEACHER, "정선생", "jung@teacher.com");
        TeacherEntity teacherEntity = TeacherEntity.builder()
                .mail("jung@teacher.com")
                .name("정선생")
                .profile(null)
                .build();
        given(teacherRepository.findByMail(request.email())).willReturn(Optional.empty());
        given(teacherMapper.toEntity(request)).willReturn(teacherEntity);

        // When: 선생님을 생성하면
        managementTeacherService.createTeacher(request);

        // Then: role이 올바르게 설정되어 저장된다
        verify(teacherRepository, times(1)).save(teacherCaptor.capture());

        TeacherEntity savedTeacher = teacherCaptor.getValue();
        assertThat(savedTeacher.getRole()).isEqualTo(Role.TEACHER);
    }

    @Test
    @DisplayName("여러 선생님을 순차적으로 생성할 수 있다")
    void shouldCreateMultipleTeachersSequentially() {
        // Given: 여러 선생님 정보가 있을 때
        TeacherRequest request1 = new TeacherRequest(Role.TEACHER, "선생1", "teacher1@school.com");
        TeacherRequest request2 = new TeacherRequest(Role.TEACHER, "선생2", "teacher2@school.com");

        TeacherEntity teacherEntity1 = TeacherEntity.builder()
                .mail("teacher1@school.com")
                .name("선생1")
                .build();
        TeacherEntity teacherEntity2 = TeacherEntity.builder()
                .mail("teacher2@school.com")
                .name("선생2")
                .build();

        given(teacherRepository.findByMail("teacher1@school.com")).willReturn(Optional.empty());
        given(teacherRepository.findByMail("teacher2@school.com")).willReturn(Optional.empty());
        given(teacherMapper.toEntity(request1)).willReturn(teacherEntity1);
        given(teacherMapper.toEntity(request2)).willReturn(teacherEntity2);

        // When: 선생님들을 순차적으로 생성하면
        managementTeacherService.createTeacher(request1);
        managementTeacherService.createTeacher(request2);

        // Then: 모든 선생님이 저장된다
        verify(teacherRepository, times(1)).findByMail("teacher1@school.com");
        verify(teacherRepository, times(1)).findByMail("teacher2@school.com");
        verify(teacherRepository, times(2)).save(any(TeacherEntity.class));
    }
}
