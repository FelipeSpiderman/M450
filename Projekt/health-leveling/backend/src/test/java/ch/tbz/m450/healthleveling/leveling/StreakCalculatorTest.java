package ch.tbz.m450.healthleveling.leveling;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StreakCalculatorTest {

    private static final LocalDate START = LocalDate.of(2026, 10, 1);

    private final StreakCalculator calculator = new StreakCalculator();

    @Test
    void bonusXp_noGoalDays_isZero() {
        assertThat(calculator.bonusXp(Set.of())).isZero();
    }

    @Test
    void bonusXp_sixDaysInARow_isZero() {
        assertThat(calculator.bonusXp(daysInARow(START, 6))).isZero();
    }

    @Test
    void bonusXp_sevenDaysInARow_gives50() {
        assertThat(calculator.bonusXp(daysInARow(START, 7))).isEqualTo(50);
    }

    @Test
    void bonusXp_fourteenDaysInARow_gives100() {
        assertThat(calculator.bonusXp(daysInARow(START, 14))).isEqualTo(100);
    }

    @Test
    void bonusXp_gapResetsTheStreak() {
        Set<LocalDate> days = daysInARow(START, 4);
        days.addAll(daysInARow(START.plusDays(5), 4));

        assertThat(calculator.bonusXp(days)).isZero();
    }

    @Test
    void longestStreak_findsLongestRun() {
        Set<LocalDate> days = daysInARow(START, 3);
        days.addAll(daysInARow(START.plusDays(10), 5));

        assertThat(calculator.longestStreak(days)).isEqualTo(5);
        assertThat(calculator.longestStreak(Set.of())).isZero();
    }

    @Test
    void currentStreak_countsBackFromToday() {
        LocalDate today = START.plusDays(4);

        assertThat(calculator.currentStreak(daysInARow(START, 5), today)).isEqualTo(5);
    }

    @Test
    void currentStreak_todayNotReachedYet_countsUntilYesterday() {
        LocalDate today = START.plusDays(5);

        assertThat(calculator.currentStreak(daysInARow(START, 5), today)).isEqualTo(5);
    }

    @Test
    void currentStreak_yesterdayMissed_isZero() {
        LocalDate today = START.plusDays(6);

        assertThat(calculator.currentStreak(daysInARow(START, 5), today)).isZero();
    }

    private Set<LocalDate> daysInARow(LocalDate first, int count) {
        Set<LocalDate> days = new HashSet<>();
        for (int i = 0; i < count; i++) {
            days.add(first.plusDays(i));
        }
        return days;
    }
}
