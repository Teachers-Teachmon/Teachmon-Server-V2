package solvit.teachmon.domain.after_school.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolBusinessTripEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.service.AfterSchoolStudentDomainService;
import solvit.teachmon.domain.after_school.exception.AfterSchoolNotFoundException;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolBusinessTripRequestDto;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.management.teacher.domain.repository.SupervisionBanDayRepository;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 출장 서비스 테스트")
class AfterSchoolServiceBusinessTripTest {

    @Mock
    private AfterSchoolStudentDomainService afterSchoolStudentDomainService;
    @Mock
    private AfterSchoolScheduleService afterSchoolScheduleService;
    @Mock
    private SupervisionBanDayRepository supervisionBanDayRepository;
    @Mock
    private AfterSchoolRepository afterSchoolRepository;
    @Mock
    private AfterSchoolBusinessTripRepository afterSchoolBusinessTripRepository;
    @Mock
    private AfterSchoolReinforcementRepository afterSchoolReinforcementRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private PlaceRepository placeRepository;

    private AfterSchoolService afterSchoolService;
    private AfterSchoolEntity afterSchool;
    private AfterSchoolBusinessTripRequestDto businessTripRequest;

    @BeforeEach
    void setUp() {
        afterSchoolService = new AfterSchoolService(
                afterSchoolStudentDomainService,
                supervisionBanDayRepository,
                afterSchoolRepository,
                afterSchoolBusinessTripRepository,
                afterSchoolReinforcementRepository,
                teacherRepository,
                studentRepository,
                branchRepository,
                placeRepository,
                afterSchoolScheduleService
        );

        // Mock을 사용해서 AfterSchoolEntity 생성
        afterSchool = mock(AfterSchoolEntity.class);

        businessTripRequest = new AfterSchoolBusinessTripRequestDto(
                LocalDate.now().plusDays(10), // 현재 날짜보다 10일 후
                1L
        );
    }

    @Test
    @DisplayName("유효한 요청으로 출장을 성공적으로 생성한다")
    void shouldCreateBusinessTripSuccessfully() {
        // Given
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.of(afterSchool));
        given(afterSchoolBusinessTripRepository.save(any(AfterSchoolBusinessTripEntity.class)))
                .willReturn(any(AfterSchoolBusinessTripEntity.class));

        // When
        afterSchoolService.createBusinessTrip(businessTripRequest);

        // Then
        verify(afterSchoolRepository).findWithAllRelations(1L);
        verify(afterSchoolBusinessTripRepository).save(any(AfterSchoolBusinessTripEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 방과후 ID로 출장 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenAfterSchoolNotExists() {
        // Given
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> afterSchoolService.createBusinessTrip(businessTripRequest))
                .isInstanceOf(AfterSchoolNotFoundException.class);

        verify(afterSchoolRepository).findWithAllRelations(1L);
        verify(afterSchoolBusinessTripRepository, never()).save(any());
    }
}
