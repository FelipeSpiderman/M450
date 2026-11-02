package ch.tbz.m450.healthleveling.leveling;

public record ProfileResponse(int level, int totalXp, int xpIntoLevel, int xpForNextLevel,
                              int progressPercent, int currentStreak) {
}
