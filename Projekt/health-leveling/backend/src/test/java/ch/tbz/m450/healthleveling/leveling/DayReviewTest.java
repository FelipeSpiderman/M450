package ch.tbz.m450.healthleveling.leveling;

import ch.tbz.m450.healthleveling.entry.Entry;
import ch.tbz.m450.healthleveling.entry.EntryType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DayReviewTest {

    private final DayReview dayReview = new DayReview();

    @ParameterizedTest
    @CsvSource({
            "04:59, NONE",
            "05:00, MORNING",
            "10:59, MORNING",
            "11:00, MIDDAY",
            "16:59, MIDDAY",
            "17:00, EVENING",
            "22:59, EVENING",
            "23:00, NONE"
    })
    void slotOf_mapsTimeToDaySlot(LocalTime time, DaySlot expected) {
        assertThat(dayReview.slotOf(time)).isEqualTo(expected);
    }

    @Test
    void slotBonus_noEntries_isZero() {
        assertThat(dayReview.slotBonus(List.of())).isZero();
    }

    @Test
    void slotBonus_oneSlot_isZero() {
        List<Entry> entries = List.of(meal("08:00", 300), meal("09:00", 300));

        assertThat(dayReview.slotBonus(entries)).isZero();
    }

    @Test
    void slotBonus_twoSlots_gives10() {
        List<Entry> entries = List.of(meal("08:00", 300), meal("12:00", 300));

        assertThat(dayReview.slotBonus(entries)).isEqualTo(10);
    }

    @Test
    void slotBonus_allThreeSlots_gives30() {
        List<Entry> entries = List.of(meal("08:00", 300), meal("12:00", 300), meal("19:00", 300));

        assertThat(dayReview.slotBonus(entries)).isEqualTo(30);
    }

    @Test
    void slotBonus_nightEntriesDoNotCount() {
        List<Entry> entries = List.of(meal("08:00", 300), meal("23:30", 300));

        assertThat(dayReview.slotBonus(entries)).isZero();
    }

    @Test
    void caloriePenalty_exactlyAtLimit_isZero() {
        List<Entry> entries = List.of(meal("08:00", 1500), meal("19:00", 1000));

        assertThat(dayReview.caloriePenalty(entries)).isZero();
    }

    @Test
    void caloriePenalty_overLimit_isMinus20() {
        List<Entry> entries = List.of(meal("08:00", 1500), meal("19:00", 1001));

        assertThat(dayReview.caloriePenalty(entries)).isEqualTo(-20);
    }

    @Test
    void caloriePenalty_burnedCaloriesRaiseTheLimit() {
        List<Entry> entries = List.of(meal("08:00", 1500), meal("19:00", 1400), activity("12:00", 500));

        assertThat(dayReview.caloriePenalty(entries)).isZero();
    }

    private Entry meal(String time, int calories) {
        return new Entry(EntryType.MEAL, at(time), null, null, calories, null, null);
    }

    private Entry activity(String time, int burnedCalories) {
        return new Entry(EntryType.ACTIVITY, at(time), "Run", 45, burnedCalories, null, null);
    }

    private LocalDateTime at(String time) {
        return LocalDateTime.of(2026, 10, 6, 0, 0).with(LocalTime.parse(time));
    }
}
