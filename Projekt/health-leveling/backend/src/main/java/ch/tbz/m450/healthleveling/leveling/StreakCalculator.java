package ch.tbz.m450.healthleveling.leveling;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

/** Streaks are days in a row on which the step goal was reached. */
@Component
public class StreakCalculator {

    private static final int STREAK_LENGTH = 7;
    private static final int STREAK_BONUS = 50;

    /** +50 XP on every 7th day in a row (day 7, 14, 21 ...). */
    public int bonusXp(Set<LocalDate> goalDays) {
        int bonus = 0;
        for (LocalDate day : goalDays) {
            if (streakEndingOn(day, goalDays) % STREAK_LENGTH == 0) {
                bonus += STREAK_BONUS;
            }
        }
        return bonus;
    }

    public int longestStreak(Set<LocalDate> goalDays) {
        return goalDays.stream().mapToInt(day -> streakEndingOn(day, goalDays)).max().orElse(0);
    }

    /** If the goal is not reached today (yet), the streak still counts until yesterday. */
    public int currentStreak(Set<LocalDate> goalDays, LocalDate today) {
        LocalDate lastDay = goalDays.contains(today) ? today : today.minusDays(1);
        return streakEndingOn(lastDay, goalDays);
    }

    private int streakEndingOn(LocalDate day, Set<LocalDate> goalDays) {
        int length = 0;
        while (goalDays.contains(day.minusDays(length))) {
            length++;
        }
        return length;
    }
}
