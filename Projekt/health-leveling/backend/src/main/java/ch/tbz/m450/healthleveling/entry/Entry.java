package ch.tbz.m450.healthleveling.entry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * A single manual entry. One table for all types: only the fields
 * that belong to the type are needed, the others can stay null.
 * For an ACTIVITY, calories are the burned calories.
 */
@Entity
public class Entry {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String name;
    private Integer durationMinutes;
    private Integer calories;
    private Integer amountMl;
    private Double weightKg;

    protected Entry() {
    }

    public Entry(EntryType type, LocalDateTime timestamp, String name, Integer durationMinutes,
                 Integer calories, Integer amountMl, Double weightKg) {
        this.type = type;
        this.timestamp = timestamp;
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.calories = calories;
        this.amountMl = amountMl;
        this.weightKg = weightKg;
    }

    public Long getId() {
        return id;
    }

    public EntryType getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public Integer getCalories() {
        return calories;
    }

    public Integer getAmountMl() {
        return amountMl;
    }

    public Double getWeightKg() {
        return weightKg;
    }
}
