package ch.tbz.m450.healthleveling.entry;

import java.time.LocalDateTime;

public record EntryResponse(
        Long id,
        EntryType type,
        LocalDateTime timestamp,
        String name,
        Integer durationMinutes,
        Integer calories,
        Integer amountMl,
        Double weightKg,
        int xp) {

    public static EntryResponse from(Entry entry, int xp) {
        return new EntryResponse(entry.getId(), entry.getType(), entry.getTimestamp(), entry.getName(),
                entry.getDurationMinutes(), entry.getCalories(), entry.getAmountMl(), entry.getWeightKg(), xp);
    }
}
