package solvit.teachmon.domain.after_school.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolEntity;
import solvit.teachmon.domain.after_school.domain.entity.AfterSchoolReinforcementEntity;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolBusinessTripRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolReinforcementRepository;
import solvit.teachmon.domain.after_school.domain.repository.AfterSchoolRepository;
import solvit.teachmon.domain.after_school.domain.service.AfterSchoolStudentDomainService;
import solvit.teachmon.domain.after_school.exception.AfterSchoolNotFoundException;
import solvit.teachmon.domain.after_school.exception.InvalidAfterSchoolReinforcementException;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolReinforcementRequestDto;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.management.student.domain.repository.StudentRepository;
import solvit.teachmon.domain.place.domain.entity.PlaceEntity;
import solvit.teachmon.domain.place.domain.repository.PlaceRepository;
import solvit.teachmon.domain.place.exception.PlaceNotFoundException;
import solvit.teachmon.domain.user.domain.repository.TeacherRepository;
import solvit.teachmon.global.enums.SchoolPeriod;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 보강 서비스 테스트")
class AfterSchoolServiceReinforcementTest {

    @Mock
    private AfterSchoolStudentDomainService afterSchoolStudentDomainService;
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
    private PlaceEntity place;
    private AfterSchoolReinforcementRequestDto reinforcementRequest;

    @BeforeEach
    void setUp() {
        afterSchoolService = new AfterSchoolService(
                afterSchoolStudentDomainService,
                afterSchoolRepository,
                afterSchoolBusinessTripRepository,
                afterSchoolReinforcementRepository,
                teacherRepository,
                studentRepository,
                branchRepository,
                placeRepository
        );

        // Mock을 사용해서 Entity 생성
        afterSchool = mock(AfterSchoolEntity.class);
        place = mock(PlaceEntity.class);

        reinforcementRequest = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10), // 현재 날짜보다 10일 후
                1L,
                8,
                9,
                1L
        );
    }

    @Test
    @DisplayName("유효한 8~9교시 요청으로 보강을 성공적으로 생성한다")
    void shouldCreateReinforcementSuccessfullyWith8To9Period() {
        // Given
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.of(afterSchool));
        given(placeRepository.findById(1L))
                .willReturn(Optional.of(place));
        given(afterSchoolReinforcementRepository.save(any(AfterSchoolReinforcementEntity.class)))
                .willReturn(any(AfterSchoolReinforcementEntity.class));

        // When
        afterSchoolService.createReinforcement(reinforcementRequest);

        // Then
        verify(afterSchoolRepository).findWithAllRelations(1L);
        verify(placeRepository).findById(1L);
        verify(afterSchoolReinforcementRepository).save(any(AfterSchoolReinforcementEntity.class));
    }

    @Test
    @DisplayName("유효한 10~11교시 요청으로 보강을 성공적으로 생성한다")
    void shouldCreateReinforcementSuccessfullyWith10To11Period() {
        // Given
        AfterSchoolReinforcementRequestDto request = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10),
                1L,
                10,
                11,
                1L
        );
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.of(afterSchool));
        given(placeRepository.findById(1L))
                .willReturn(Optional.of(place));
        given(afterSchoolReinforcementRepository.save(any(AfterSchoolReinforcementEntity.class)))
                .willReturn(any(AfterSchoolReinforcementEntity.class));

        // When
        afterSchoolService.createReinforcement(request);

        // Then
        verify(afterSchoolRepository).findWithAllRelations(1L);
        verify(placeRepository).findById(1L);
        verify(afterSchoolReinforcementRepository).save(any(AfterSchoolReinforcementEntity.class));
    }

    @Test
    @DisplayName("존재하지 않는 방과후 ID로 보강 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenAfterSchoolNotExists() {
        // Given
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> afterSchoolService.createReinforcement(reinforcementRequest))
                .isInstanceOf(AfterSchoolNotFoundException.class);

        verify(afterSchoolRepository).findWithAllRelations(1L);
        verify(afterSchoolReinforcementRepository, never()).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 장소 ID로 보강 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenPlaceNotExists() {
        // Given
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.of(afterSchool));
        given(placeRepository.findById(1L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> afterSchoolService.createReinforcement(reinforcementRequest))
                .isInstanceOf(PlaceNotFoundException.class);

        verify(afterSchoolRepository).findWithAllRelations(1L);
        verify(placeRepository).findById(1L);
        verify(afterSchoolReinforcementRepository, never()).save(any());
    }

    @Test
    @DisplayName("유효한 7교시 요청으로 보강을 성공적으로 생성한다")
    void shouldCreateReinforcementSuccessfullyWith7Period() {
        // Given
        AfterSchoolReinforcementRequestDto request = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10),
                1L,
                7,
                7,
                1L
        );
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.of(afterSchool));
        given(placeRepository.findById(1L))
                .willReturn(Optional.of(place));
        given(afterSchoolReinforcementRepository.save(any(AfterSchoolReinforcementEntity.class)))
                .willReturn(any(AfterSchoolReinforcementEntity.class));

        // When
        afterSchoolService.createReinforcement(request);

        // Then
        verify(afterSchoolRepository).findWithAllRelations(1L);
        verify(placeRepository).findById(1L);
        verify(afterSchoolReinforcementRepository).save(any(AfterSchoolReinforcementEntity.class));
    }

    @Test
    @DisplayName("지원하지 않는 교시(6~7)로 보강 생성 시 예외가 발생한다")
    void shouldThrowExceptionWhenUnsupportedPeriod6To7() {
        // Given
        AfterSchoolReinforcementRequestDto invalidRequest = new AfterSchoolReinforcementRequestDto(
                LocalDate.now().plusDays(10),
                1L,
                6,
                7,
                1L
        );
        given(afterSchoolRepository.findWithAllRelations(1L))
                .willReturn(Optional.of(afterSchool));
        given(placeRepository.findById(1L))
                .willReturn(Optional.of(place));

        // When & Then
        assertThatThrownBy(() -> afterSchoolService.createReinforcement(invalidRequest))
                .isInstanceOf(InvalidAfterSchoolReinforcementException.class)
                .hasMessageContaining("지원하지 않는 교시입니다: 6~7");

        verify(afterSchoolReinforcementRepository, never()).save(any());
    }
}