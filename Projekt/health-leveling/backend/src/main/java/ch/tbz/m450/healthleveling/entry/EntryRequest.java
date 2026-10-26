package ch.tbz.m450.healthleveling.entry;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/** Ranges are checked here. Which fields are required depends on the type, see EntryService. */
public record EntryRequest(
        @NotNull EntryType type,
        @NotNull @PastOrPresent LocalDateTime timestamp,
        @Size(max = 100) String name,
        @Min(1) @Max(1440) Integer durationMinutes,
        @Min(0) @Max(5000) Integer calories,
        @Min(1) @Max(5000) Integer amountMl,
        @DecimalMin("20") @DecimalMax("400") Double weightKg) {
}
