package ch.tbz.m450.healthleveling.leveling;

public record LevelProgress(int level, int totalXp, int xpIntoLevel, int xpForNextLevel, int progressPercent) {
}
