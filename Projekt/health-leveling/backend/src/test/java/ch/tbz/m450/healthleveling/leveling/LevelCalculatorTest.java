package ch.tbz.m450.healthleveling.leveling;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LevelCalculatorTest {

    private final LevelCalculator calculator = new LevelCalculator();

    @ParameterizedTest
    @CsvSource({
            "0, 1",
            "99, 1",
            "100, 2",
            "299, 2",
            "300, 3",
            "599, 3",
            "600, 4"
    })
    void levelFor_returnsLevelForTotalXp(int totalXp, int expectedLevel) {
        assertThat(calculator.levelFor(totalXp)).isEqualTo(expectedLevel);
    }

    @ParameterizedTest
    @CsvSource({
            "1, 0",
            "2, 100",
            "3, 300",
            "4, 600",
            "5, 1000"
    })
    void xpRequiredFor_returnsTotalXpNeededToReachLevel(int level, int expectedXp) {
        assertThat(calculator.xpRequiredFor(level)).isEqualTo(expectedXp);
    }

    @Test
    void progress_returnsXpIntoCurrentLevelAndPercent() {
        LevelProgress progress = calculator.progress(150);

        assertThat(progress.level()).isEqualTo(2);
        assertThat(progress.totalXp()).isEqualTo(150);
        assertThat(progress.xpIntoLevel()).isEqualTo(50);
        assertThat(progress.xpForNextLevel()).isEqualTo(200);
        assertThat(progress.progressPercent()).isEqualTo(25);
    }

    @Test
    void progress_atZeroXp_isLevelOneWithZeroPercent() {
        LevelProgress progress = calculator.progress(0);

        assertThat(progress.level()).isEqualTo(1);
        assertThat(progress.progressPercent()).isZero();
    }

    @Test
    void levelFor_negativeXp_throws() {
        assertThatThrownBy(() -> calculator.levelFor(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
