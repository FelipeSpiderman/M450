package ch.tbz.m450.healthleveling.day;

import java.time.LocalDate;

/**
 * Daily values from any source (manual, import, Google).
 * Validated in the constructor, so an invalid object can never exist.
 */
public record DailyValues(LocalDate date, Integer steps, Integer sleepScore,
                          Integer activityCount, Integer activityMinutes) {

    public DailyValues {
        if (date == null) {
            throw new IllegalArgumentException("date is required");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("date must not be in the future");
        }
        checkRange("steps", steps, 0, 100000);
        checkRange("sleepScore", sleepScore, 0, 100);
        checkRange("activityCount", activityCount, 0, 50);
        checkRange("activityMinutes", activityMinutes, 0, 1440);
    }

    private static void checkRange(String field, Integer value, int min, int max) {
        if (value != null && (value < min || value > max)) {
            throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
        }
    }
}
