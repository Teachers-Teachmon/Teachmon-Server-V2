package solvit.teachmon.domain.after_school.presentation.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import solvit.teachmon.domain.after_school.application.service.AfterSchoolService;
import solvit.teachmon.domain.after_school.exception.AfterSchoolNotFoundException;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolDeleteRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolCreateRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolUpdateRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.request.AfterSchoolSearchRequestDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolResponseDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolMyResponseDto;
import solvit.teachmon.domain.after_school.presentation.dto.response.AfterSchoolTodayResponseDto;
import solvit.teachmon.global.enums.WeekDay;
import solvit.teachmon.global.security.user.TeachmonUserDetails;
import solvit.teachmon.domain.user.domain.entity.TeacherEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("방과후 컨트롤러 테스트")
class AfterSchoolControllerTest {

    @InjectMocks
    private AfterSchoolController afterSchoolController;

    @Mock
    private AfterSchoolService afterSchoolService;
    
    @Mock
    private TeachmonUserDetails teachmonUserDetails;
    
    @Mock
    private TeacherEntity teacherEntity;

    @Test
    @DisplayName("방과후 생성 시 204 상태코드를 반환한다")
    void shouldCreateAfterSchoolSuccessfully() {
        AfterSchoolCreateRequestDto request = new AfterSchoolCreateRequestDto(
                2024,
                2,
                "MON",
                "EIGHT_AND_NINE_PERIOD",
                1L,
                1L,
                "정보처리 산업기사 Java",
                List.of(1L, 2L)
        );
        willDoNothing().given(afterSchoolService).createAfterSchool(any(AfterSchoolCreateRequestDto.class));

        ResponseEntity<Void> response = afterSchoolController.createAfterSchool(request);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(afterSchoolService).createAfterSchool(any(AfterSchoolCreateRequestDto.class));
    }

    @Test
    @DisplayName("방과후 수정 시 200 상태코드를 반환한다")
    void shouldUpdateAfterSchoolSuccessfully() {
        AfterSchoolUpdateRequestDto request = new AfterSchoolUpdateRequestDto(
                1L,
                2024,
                3,
                "TUE",
                "EIGHT_AND_NINE_PERIOD",
                2L,
                2L,
                "웹 개발 기초",
                List.of(3L, 4L)
        );
        willDoNothing().given(afterSchoolService).updateAfterSchool(any(AfterSchoolUpdateRequestDto.class));

        ResponseEntity<Void> response = afterSchoolController.updateAfterSchool(request);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(afterSchoolService).updateAfterSchool(any(AfterSchoolUpdateRequestDto.class));
    }

    @Test
    @DisplayName("존재하지 않는 방과후 수정 시 예외가 발생한다")
    void shouldThrowExceptionWhenAfterSchoolNotFoundInUpdate() {
        AfterSchoolUpdateRequestDto request = new AfterSchoolUpdateRequestDto(
                999L,
                2024,
                2,
                "MON",
                "EIGHT_AND_NINE_PERIOD",
                1L,
                1L,
                "정보처리 산업기사 Java",
                List.of(1L, 2L)
        );
        willThrow(new AfterSchoolNotFoundException(999L)).given(afterSchoolService).updateAfterSchool(any());

        assertThatThrownBy(() -> afterSchoolController.updateAfterSchool(request))
                .isInstanceOf(AfterSchoolNotFoundException.class);
    }

    @Test
    @DisplayName("방과후 삭제 시 200 상태코드를 반환한다")
    void shouldDeleteAfterSchoolSuccessfully() {
        AfterSchoolDeleteRequestDto request = new AfterSchoolDeleteRequestDto(1L);
        willDoNothing().given(afterSchoolService).deleteAfterSchool(1L);

        ResponseEntity<Void> response = afterSchoolController.deleteAfterSchool(request);

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(afterSchoolService).deleteAfterSchool(1L);
    }

    @Test
    @DisplayName("존재하지 않는 방과후 삭제 시 예외가 발생한다")
    void shouldThrowExceptionWhenAfterSchoolNotFoundInDelete() {
        AfterSchoolDeleteRequestDto request = new AfterSchoolDeleteRequestDto(999L);
        willThrow(new AfterSchoolNotFoundException(999L)).given(afterSchoolService).deleteAfterSchool(999L);

        assertThatThrownBy(() -> afterSchoolController.deleteAfterSchool(request))
                .isInstanceOf(AfterSchoolNotFoundException.class);
    }

    @Test
    @DisplayName("방과후 검색 시 200 상태코드와 결과 리스트를 반환한다")
    void shouldSearchAfterSchoolsSuccessfully() {
        Integer grade = 2;
        WeekDay weekDay = WeekDay.TUE;
        Integer startPeriod = 8;
        Integer endPeriod = 9;
        
        AfterSchoolResponseDto responseDto = new AfterSchoolResponseDto(
                1L,
                "화",
                "8~9교시",
                "파이썬을 이용한 문제해결",
                new AfterSchoolResponseDto.TeacherInfo(1L, "곽상미"),
                new AfterSchoolResponseDto.PlaceInfo(1L, "객체지향 프로그래밍실")
        );
        
        given(afterSchoolService.searchAfterSchools(any(AfterSchoolSearchRequestDto.class)))
                .willReturn(List.of(responseDto));

        ResponseEntity<List<AfterSchoolResponseDto>> response = afterSchoolController.searchAfterSchools(
                grade, weekDay, startPeriod, endPeriod
        );
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).id()).isEqualTo(1L);
        assertThat(response.getBody().get(0).name()).isEqualTo("파이썬을 이용한 문제해결");
        
        verify(afterSchoolService).searchAfterSchools(any(AfterSchoolSearchRequestDto.class));
    }

    @Test
    @DisplayName("나의 방과후 검색 시 200 상태코드와 결과 리스트를 반환한다")
    void shouldSearchMyAfterSchoolsSuccessfully() {
        Integer grade = 1;
        
        AfterSchoolMyResponseDto responseDto = new AfterSchoolMyResponseDto(
                1L,
                "월",
                "8~9교시",
                "파이썬을 이용한 문제해결",
                new AfterSchoolMyResponseDto.PlaceInfo(1L, "객체지향 프로그래밍실"),
                0
        );
        
        given(afterSchoolService.searchMyAfterSchools(anyLong(), eq(grade)))
                .willReturn(List.of(responseDto));

        given(teachmonUserDetails.getId()).willReturn(1L);
        
        ResponseEntity<List<AfterSchoolMyResponseDto>> response = afterSchoolController.searchMyAfterSchools(grade, teachmonUserDetails);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).id()).isEqualTo(1L);
        assertThat(response.getBody().get(0).name()).isEqualTo("파이썬을 이용한 문제해결");
        assertThat(response.getBody().get(0).reinforcementCount()).isEqualTo(0);
        
        verify(afterSchoolService).searchMyAfterSchools(anyLong(), eq(grade));
    }

    @Test
    @DisplayName("나의 방과후 검색에서 grade 조건 없이 검색 시 모든 결과를 반환한다")
    void shouldSearchMyAfterSchoolsWithoutGradeFilter() {
        AfterSchoolMyResponseDto responseDto1 = new AfterSchoolMyResponseDto(
                1L,
                "월",
                "8~9교시",
                "파이썬을 이용한 문제해결",
                new AfterSchoolMyResponseDto.PlaceInfo(1L, "객체지향 프로그래밍실"),
                0
        );
        
        AfterSchoolMyResponseDto responseDto2 = new AfterSchoolMyResponseDto(
                2L,
                "화",
                "7교시",
                "Java 프로그래밍",
                new AfterSchoolMyResponseDto.PlaceInfo(2L, "컴퓨터실1"),
                2
        );
        
        given(afterSchoolService.searchMyAfterSchools(anyLong(), isNull()))
                .willReturn(List.of(responseDto1, responseDto2));

        given(teachmonUserDetails.getId()).willReturn(1L);
        
        ResponseEntity<List<AfterSchoolMyResponseDto>> response = afterSchoolController.searchMyAfterSchools(null, teachmonUserDetails);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(2);
        
        verify(afterSchoolService).searchMyAfterSchools(anyLong(), isNull());
    }

    @Test
    @DisplayName("나의 오늘 방과후 검색 시 200 상태코드와 결과 리스트를 반환한다")
    void shouldSearchMyTodayAfterSchoolsSuccessfully() {
        AfterSchoolTodayResponseDto responseDto1 = new AfterSchoolTodayResponseDto(
                1L,
                3,
                "파이썬을 이용한 문제해결",
                new AfterSchoolTodayResponseDto.PlaceInfo(1L, "객체지향 프로그래밍실"),
                2,
                "8~9교시",
                "2025.01.29 수요일"
        );
        
        AfterSchoolTodayResponseDto responseDto2 = new AfterSchoolTodayResponseDto(
                2L,
                3,
                "파이썬을 이용한 문제해결22",
                new AfterSchoolTodayResponseDto.PlaceInfo(2L, "객체지향 프로그래밍실2"),
                1,
                "10~11교시",
                "2025.01.29 수요일"
        );
        
        given(afterSchoolService.searchMyTodayAfterSchools(anyLong()))
                .willReturn(List.of(responseDto1, responseDto2));

        given(teachmonUserDetails.getId()).willReturn(1L);
        
        ResponseEntity<List<AfterSchoolTodayResponseDto>> response = afterSchoolController.searchMyTodayAfterSchools(teachmonUserDetails);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(2);
        
        AfterSchoolTodayResponseDto firstResult = response.getBody().get(0);
        assertThat(firstResult.id()).isEqualTo(1L);
        assertThat(firstResult.branch()).isEqualTo(3);
        assertThat(firstResult.name()).isEqualTo("파이썬을 이용한 문제해결");
        assertThat(firstResult.grade()).isEqualTo(2);
        assertThat(firstResult.period()).isEqualTo("8~9교시");
        assertThat(firstResult.day()).isEqualTo("2025.01.29 수요일");
        
        AfterSchoolTodayResponseDto secondResult = response.getBody().get(1);
        assertThat(secondResult.id()).isEqualTo(2L);
        assertThat(secondResult.grade()).isEqualTo(1);
        assertThat(secondResult.period()).isEqualTo("10~11교시");
        
        verify(afterSchoolService).searchMyTodayAfterSchools(anyLong());
    }

    @Test
    @DisplayName("나의 오늘 방과후가 없는 경우 빈 리스트를 반환한다")
    void shouldReturnEmptyListWhenNoTodayAfterSchools() {
        given(afterSchoolService.searchMyTodayAfterSchools(anyLong()))
                .willReturn(List.of());

        given(teachmonUserDetails.getId()).willReturn(1L);
        
        ResponseEntity<List<AfterSchoolTodayResponseDto>> response = afterSchoolController.searchMyTodayAfterSchools(teachmonUserDetails);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEmpty();
        
        verify(afterSchoolService).searchMyTodayAfterSchools(anyLong());
    }
}
