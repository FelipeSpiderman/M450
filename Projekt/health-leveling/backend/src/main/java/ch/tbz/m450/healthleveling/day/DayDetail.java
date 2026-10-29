package ch.tbz.m450.healthleveling.day;

import ch.tbz.m450.healthleveling.entry.EntryResponse;

import java.time.LocalDate;
import java.util.List;

public record DayDetail(
        LocalDate date,
        Integer steps,
        Integer sleepScore,
        Integer activityCount,
        Integer activityMinutes,
        List<EntryResponse> entries,
        int valuesXp,
        int entriesXp,
        int slotBonus,
        int caloriePenalty,
        int totalXp) {
}
