package ch.tbz.m450.healthleveling.leveling;

import ch.tbz.m450.healthleveling.day.DayDetail;
import ch.tbz.m450.healthleveling.day.DayService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** The profile is not stored: everything is calculated from the saved days. */
@Service
public class ProfileService {

    private final DayService dayService;
    private final LevelCalculator levelCalculator;
    private final StreakCalculator streakCalculator;

    public ProfileService(DayService dayService, LevelCalculator levelCalculator, StreakCalculator streakCalculator) {
        this.dayService = dayService;
        this.levelCalculator = levelCalculator;
        this.streakCalculator = streakCalculator;
    }

    public ProfileResponse getProfile() {
        List<DayDetail> days = dayService.getDays();
        LevelProgress progress = levelCalculator.progress(totalXp(days));
        int currentStreak = streakCalculator.currentStreak(stepGoalDays(days), LocalDate.now());
        return new ProfileResponse(progress.level(), progress.totalXp(), progress.xpIntoLevel(),
                progress.xpForNextLevel(), progress.progressPercent(), currentStreak);
    }

    public List<AchievementStatus> getAchievements() {
        List<DayDetail> days = dayService.getDays();
        int level = levelCalculator.levelFor(totalXp(days));
        int longestStreak = streakCalculator.longestStreak(stepGoalDays(days));

        return Arrays.stream(Achievement.values())
                .map(achievement -> new AchievementStatus(achievement, achievement.getTitle(),
                        achievement.getDescription(), isUnlocked(achievement, days, level, longestStreak)))
                .toList();
    }

    private boolean isUnlocked(Achievement achievement, List<DayDetail> days, int level, int longestStreak) {
        return switch (achievement) {
            case FIRST_ENTRY -> days.stream().anyMatch(day -> !day.entries().isEmpty());
            case STEP_GOAL -> !stepGoalDays(days).isEmpty();
            case GOOD_SLEEP -> days.stream().anyMatch(day -> day.sleepScore() != null && day.sleepScore() >= 90);
            case FULL_DAY -> days.stream().anyMatch(day -> day.slotBonus() == DayReview.FULL_DAY_BONUS);
            case WEEK_STREAK -> longestStreak >= 7;
            case LEVEL_5 -> level >= 5;
        };
    }

    /** XP of all days plus the streak bonus, never below 0. */
    private int totalXp(List<DayDetail> days) {
        int sum = days.stream().mapToInt(DayDetail::totalXp).sum() + streakCalculator.bonusXp(stepGoalDays(days));
        return Math.max(0, sum);
    }

    private Set<LocalDate> stepGoalDays(List<DayDetail> days) {
        return days.stream()
                .filter(day -> day.steps() != null && day.steps() >= XpCalculator.STEP_GOAL)
                .map(DayDetail::date)
                .collect(Collectors.toSet());
    }
}
