package ch.tbz.m450.healthleveling.day;

import ch.tbz.m450.healthleveling.entry.Entry;
import ch.tbz.m450.healthleveling.entry.EntryRepository;
import ch.tbz.m450.healthleveling.entry.EntryType;
import ch.tbz.m450.healthleveling.leveling.DayReview;
import ch.tbz.m450.healthleveling.leveling.XpCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DayServiceTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 6);

    @Mock
    private DailyRecordRepository recordRepository;

    @Mock
    private EntryRepository entryRepository;

    private DayService service;

    @BeforeEach
    void setUp() {
        // only the repositories are mocked, the XP rules are real
        service = new DayService(recordRepository, entryRepository, new XpCalculator(), new DayReview());
    }

    @Test
    void saveAll_newDay_createsRecord() {
        when(recordRepository.findByDate(DAY)).thenReturn(Optional.empty());

        ImportResult result = service.saveAll(List.of(new DailyValues(DAY, 8000, 70, 1, 30)));

        assertThat(result).isEqualTo(new ImportResult(1, 0));
        ArgumentCaptor<DailyRecord> captor = ArgumentCaptor.forClass(DailyRecord.class);
        verify(recordRepository).save(captor.capture());
        assertThat(captor.getValue().getSteps()).isEqualTo(8000);
    }

    @Test
    void saveAll_existingDay_overwritesOnlyGivenValues() {
        DailyRecord existing = record(DAY, 8000, 70);
        when(recordRepository.findByDate(DAY)).thenReturn(Optional.of(existing));

        ImportResult result = service.saveAll(List.of(new DailyValues(DAY, 9000, null, null, null)));

        assertThat(result).isEqualTo(new ImportResult(0, 1));
        assertThat(existing.getSteps()).isEqualTo(9000);
        assertThat(existing.getSleepScore()).isEqualTo(70);
    }

    @Test
    void getDay_combinesValuesEntriesAndBonus() {
        when(recordRepository.findByDate(DAY)).thenReturn(Optional.of(record(DAY, 10000, null)));
        when(entryRepository.findByDate(DAY)).thenReturn(List.of(meal(8, 600), meal(12, 800), meal(19, 700)));

        DayDetail day = service.getDay(DAY);

        assertThat(day.steps()).isEqualTo(10000);
        assertThat(day.entries()).hasSize(3);
        assertThat(day.valuesXp()).isEqualTo(70);
        assertThat(day.entriesXp()).isEqualTo(30);
        assertThat(day.slotBonus()).isEqualTo(30);
        assertThat(day.caloriePenalty()).isZero();
        assertThat(day.totalXp()).isEqualTo(130);
    }

    @Test
    void getDay_withoutData_returnsEmptyDay() {
        when(recordRepository.findByDate(DAY)).thenReturn(Optional.empty());
        when(entryRepository.findByDate(DAY)).thenReturn(List.of());

        DayDetail day = service.getDay(DAY);

        assertThat(day.steps()).isNull();
        assertThat(day.totalXp()).isZero();
    }

    @Test
    void getDays_includesDaysWithOnlyValuesOrOnlyEntries_newestFirst() {
        LocalDate dayBefore = DAY.minusDays(1);
        when(recordRepository.findAll()).thenReturn(List.of(record(dayBefore, null, 80)));
        when(entryRepository.findAll()).thenReturn(List.of(meal(8, 500)));

        List<DayDetail> days = service.getDays();

        assertThat(days).extracting(DayDetail::date).containsExactly(DAY, dayBefore);
        assertThat(days).extracting(DayDetail::totalXp).containsExactly(10, 40);
        assertThat(service.totalXp()).isEqualTo(50);
    }

    @Test
    void totalXp_neverNegative() {
        when(recordRepository.findAll()).thenReturn(List.of());
        // one huge meal: 10 XP - 20 penalty
        when(entryRepository.findAll()).thenReturn(List.of(meal(8, 4000)));

        assertThat(service.totalXp()).isZero();
    }

    private DailyRecord record(LocalDate date, Integer steps, Integer sleepScore) {
        DailyRecord record = new DailyRecord(date);
        record.update(new DailyValues(date, steps, sleepScore, null, null));
        return record;
    }

    private Entry meal(int hour, int calories) {
        return new Entry(EntryType.MEAL, DAY.atTime(hour, 0), null, null, calories, null, null);
    }
}
