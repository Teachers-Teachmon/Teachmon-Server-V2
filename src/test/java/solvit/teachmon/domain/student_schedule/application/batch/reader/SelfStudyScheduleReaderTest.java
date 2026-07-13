package solvit.teachmon.domain.student_schedule.application.batch.reader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import solvit.teachmon.domain.branch.domain.entity.BranchEntity;
import solvit.teachmon.domain.branch.domain.repository.BranchRepository;
import solvit.teachmon.domain.branch.exception.BranchNotFoundException;
import solvit.teachmon.domain.self_study.domain.entity.SelfStudyEntity;
import solvit.teachmon.domain.self_study.domain.repository.SelfStudyRepository;
import solvit.teachmon.domain.student_schedule.application.batch.support.BatchTestFixtures;
import solvit.teachmon.global.enums.WeekDay;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("SelfStudyScheduleReader 테스트")
class SelfStudyScheduleReaderTest {

    @Mock
    private SelfStudyRepository selfStudyRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private SelfStudyScheduleReader reader;

    @Test
    @DisplayName("baseDate 이전 요일 데이터는 제외하고 읽는다")
    void shouldFilterOutSelfStudyBeforeBaseDate() throws Exception {
        LocalDate baseDate = LocalDate.of(2026, 4, 8); // 수요일
        BatchTestFixtures.setField(reader, "baseDate", baseDate);

        BranchEntity branch = mock(BranchEntity.class);
        SelfStudyEntity mondaySelfStudy = mock(SelfStudyEntity.class);
        SelfStudyEntity thursdaySelfStudy = mock(SelfStudyEntity.class);

        given(mondaySelfStudy.getWeekDay()).willReturn(WeekDay.MON); // baseDate보다 이전
        given(thursdaySelfStudy.getWeekDay()).willReturn(WeekDay.THU); // baseDate 이후

        given(branchRepository.findByDay(baseDate)).willReturn(Optional.of(branch));
        given(selfStudyRepository.findAllByBranchWithPessimisticRead(branch))
                .willReturn(List.of(mondaySelfStudy, thursdaySelfStudy));

        assertThat(reader.read()).isSameAs(thursdaySelfStudy);
        assertThat(reader.read()).isNull();
    }

    @Test
    @DisplayName("분기 정보가 없으면 예외를 던진다")
    void shouldThrowWhenBranchNotFound() {
        LocalDate baseDate = LocalDate.of(2026, 4, 8);
        BatchTestFixtures.setField(reader, "baseDate", baseDate);

        given(branchRepository.findByDay(baseDate)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reader.read())
                .isInstanceOf(BranchNotFoundException.class);
    }
}

