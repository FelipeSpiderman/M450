package ch.tbz.m450.healthleveling.leveling;

import org.springframework.stereotype.Component;

/**
 * Going from level n to n+1 costs 100 * n XP.
 * So the total XP to reach level L is 100 * (1 + 2 + ... + (L-1)) = 50 * L * (L-1).
 */
@Component
public class LevelCalculator {

    public int xpRequiredFor(int level) {
        return 50 * level * (level - 1);
    }

    public int levelFor(int totalXp) {
        if (totalXp < 0) {
            throw new IllegalArgumentException("XP must not be negative");
        }
        int level = 1;
        while (xpRequiredFor(level + 1) <= totalXp) {
            level++;
        }
        return level;
    }

    public LevelProgress progress(int totalXp) {
        int level = levelFor(totalXp);
        int xpIntoLevel = totalXp - xpRequiredFor(level);
        int xpForNextLevel = 100 * level;
        return new LevelProgress(level, totalXp, xpIntoLevel, xpForNextLevel, xpIntoLevel * 100 / xpForNextLevel);
    }
}
