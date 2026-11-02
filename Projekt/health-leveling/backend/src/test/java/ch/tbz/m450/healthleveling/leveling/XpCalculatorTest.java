package ch.tbz.m450.healthleveling.leveling;

import ch.tbz.m450.healthleveling.day.DailyRecord;
import ch.tbz.m450.healthleveling.day.DailyValues;
import ch.tbz.m450.healthleveling.entry.Entry;
import ch.tbz.m450.healthleveling.entry.EntryType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class XpCalculatorTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 6);
    private static final LocalDateTime TIME = DAY.atTime(12, 0);

    private final XpCalculator calculator = new XpCalculator();

    @ParameterizedTest
    @CsvSource({"0, 0", "999, 0", "1000, 5", "9999, 45", "10000, 70", "20000, 120", "35000, 120"})
    void stepsXp_givesFivePerThousandAndBonusFromTenThousand(int steps, int expectedXp) {
        assertThat(calculator.stepsXp(steps)).isEqualTo(expectedXp);
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "1, 0", "75, 37", "100, 50"})
    void sleepXp_isHalfTheScore(int score, int expectedXp) {
        assertThat(calculator.sleepXp(score)).isEqualTo(expectedXp);
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "1, 11", "15, 25", "60, 70", "120, 130", "121, 130", "600, 130"})
    void activityXp_givesBasePlusOnePerMinuteCappedAt120(int minutes, int expectedXp) {
        assertThat(calculator.activityXp(minutes)).isEqualTo(expectedXp);
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "249, 0", "250, 5", "1000, 20", "3000, 20"})
    void drinkXp_givesFivePer250mlCappedAt20(int amountMl, int expectedXp) {
        assertThat(calculator.drinkXp(amountMl)).isEqualTo(expectedXp);
    }

    @Test
    void dailyRecordXp_sumsAllValues() {
        // 70 (steps) + 40 (sleep) + 2 * 10 + 69 (activities)
        assertThat(calculator.dailyRecordXp(record(10000, 80, 2, 69))).isEqualTo(199);
    }

    @Test
    void dailyRecordXp_withMissingValues_countsOnlyPresentOnes() {
        assertThat(calculator.dailyRecordXp(record(null, 60, null, null))).isEqualTo(30);
    }

    @Test
    void dailyRecordXp_capsImportedActivityMinutes() {
        assertThat(calculator.dailyRecordXp(record(null, null, 1, 300))).isEqualTo(130);
    }

    @ParameterizedTest
    @CsvSource({"ACTIVITY, 40", "MEAL, 10", "DRINK, 10", "WEIGHT, 5"})
    void entryXp_dependsOnType(EntryType type, int expectedXp) {
        Entry entry = new Entry(type, TIME, "Run", 30, 800, 500, 72.5);

        assertThat(calculator.entryXp(entry)).isEqualTo(expectedXp);
    }

    private DailyRecord record(Integer steps, Integer sleepScore, Integer activityCount, Integer activityMinutes) {
        DailyRecord record = new DailyRecord(DAY);
        record.update(new DailyValues(DAY, steps, sleepScore, activityCount, activityMinutes));
        return record;
    }
}
