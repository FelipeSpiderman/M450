package ch.tbz.m450.healthleveling.leveling;

import ch.tbz.m450.healthleveling.entry.Entry;
import ch.tbz.m450.healthleveling.entry.EntryType;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

/** Evaluates a whole day: rewards entries spread over the day, punishes eating too much. */
@Component
public class DayReview {

    private static final LocalTime MORNING_START = LocalTime.of(5, 0);
    private static final LocalTime MIDDAY_START = LocalTime.of(11, 0);
    private static final LocalTime EVENING_START = LocalTime.of(17, 0);
    private static final LocalTime NIGHT_START = LocalTime.of(23, 0);

    private static final int RECOMMENDED_CALORIES = 2500;
    private static final int CALORIE_PENALTY = -20;

    public DaySlot slotOf(LocalTime time) {
        if (time.isBefore(MORNING_START) || !time.isBefore(NIGHT_START)) {
            return DaySlot.NONE;
        }
        if (time.isBefore(MIDDAY_START)) {
            return DaySlot.MORNING;
        }
        if (time.isBefore(EVENING_START)) {
            return DaySlot.MIDDAY;
        }
        return DaySlot.EVENING;
    }

    public int slotBonus(List<Entry> entries) {
        long slots = entries.stream()
                .map(entry -> slotOf(entry.getTimestamp().toLocalTime()))
                .filter(slot -> slot != DaySlot.NONE)
                .distinct()
                .count();
        if (slots == 3) {
            return 30;
        }
        if (slots == 2) {
            return 10;
        }
        return 0;
    }

    public int caloriePenalty(List<Entry> entries) {
        int eaten = sumCalories(entries, EntryType.MEAL);
        int burned = sumCalories(entries, EntryType.ACTIVITY);
        return eaten > RECOMMENDED_CALORIES + burned ? CALORIE_PENALTY : 0;
    }

    private int sumCalories(List<Entry> entries, EntryType type) {
        return entries.stream()
                .filter(entry -> entry.getType() == type && entry.getCalories() != null)
                .mapToInt(Entry::getCalories)
                .sum();
    }
}
