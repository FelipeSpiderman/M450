package ch.tbz.m450.healthleveling.leveling;

import ch.tbz.m450.healthleveling.day.DailyRecord;
import ch.tbz.m450.healthleveling.entry.Entry;
import org.springframework.stereotype.Component;

@Component
public class XpCalculator {

    public static final int STEP_GOAL = 10000;

    private static final int MAX_ACTIVITY_MINUTES = 120;
    private static final int ACTIVITY_BASE_XP = 10;

    public int stepsXp(int steps) {
        int xp = Math.min(steps, 20000) / 1000 * 5;
        return steps >= STEP_GOAL ? xp + 20 : xp;
    }

    public int sleepXp(int sleepScore) {
        return sleepScore / 2;
    }

    public int activityXp(int minutes) {
        return minutes <= 0 ? 0 : ACTIVITY_BASE_XP + Math.min(minutes, MAX_ACTIVITY_MINUTES);
    }

    public int drinkXp(int amountMl) {
        return Math.min(amountMl / 250 * 5, 20);
    }

    public int dailyRecordXp(DailyRecord record) {
        int xp = record.getSteps() == null ? 0 : stepsXp(record.getSteps());
        xp += record.getSleepScore() == null ? 0 : sleepXp(record.getSleepScore());
        xp += orZero(record.getActivityCount()) * ACTIVITY_BASE_XP
                + Math.min(orZero(record.getActivityMinutes()), MAX_ACTIVITY_MINUTES);
        return xp;
    }

    public int entryXp(Entry entry) {
        return switch (entry.getType()) {
            case ACTIVITY -> activityXp(orZero(entry.getDurationMinutes()));
            case MEAL -> 10;
            case DRINK -> drinkXp(orZero(entry.getAmountMl()));
            case WEIGHT -> 5;
        };
    }

    private int orZero(Integer value) {
        return value == null ? 0 : value;
    }
}
