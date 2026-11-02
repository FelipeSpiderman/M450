package ch.tbz.m450.healthleveling.day;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

class DailyValuesTest {

    private static final LocalDate DAY = LocalDate.of(2026, 10, 6);

    @Test
    void acceptsBoundaryValues() {
        assertThatNoException().isThrownBy(() -> new DailyValues(DAY, 0, 0, 0, 0));
        assertThatNoException().isThrownBy(() -> new DailyValues(DAY, 100000, 100, 50, 1440));
    }

    @Test
    void acceptsMissingValues() {
        DailyValues values = new DailyValues(DAY, null, null, null, null);

        assertThat(values.steps()).isNull();
    }

    @Test
    void rejectsNegativeSteps() {
        assertThatThrownBy(() -> new DailyValues(DAY, -1, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("steps");
    }

    @Test
    void rejectsSleepScoreOver100() {
        assertThatThrownBy(() -> new DailyValues(DAY, null, 101, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sleepScore");
    }

    @Test
    void rejectsTooManyActivityMinutes() {
        assertThatThrownBy(() -> new DailyValues(DAY, null, null, 1, 1441))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("activityMinutes");
    }

    @Test
    void rejectsMissingDate() {
        assertThatThrownBy(() -> new DailyValues(null, 100, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date");
    }

    @Test
    void rejectsFutureDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        assertThatThrownBy(() -> new DailyValues(tomorrow, 100, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("future");
    }
}
