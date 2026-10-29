package ch.tbz.m450.healthleveling.day;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;

/** Values that exist once per day: steps, sleep score and tracked activities. */
@Entity
public class DailyRecord {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    // null means "no data", which is different from 0
    private Integer steps;
    private Integer sleepScore;
    private Integer activityCount;
    private Integer activityMinutes;

    protected DailyRecord() {
    }

    public DailyRecord(LocalDate date) {
        this.date = date;
    }

    /** Takes over the given values. A null value means "not provided", so the old value stays. */
    public void update(DailyValues values) {
        if (values.steps() != null) {
            steps = values.steps();
        }
        if (values.sleepScore() != null) {
            sleepScore = values.sleepScore();
        }
        if (values.activityCount() != null) {
            activityCount = values.activityCount();
        }
        if (values.activityMinutes() != null) {
            activityMinutes = values.activityMinutes();
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getSteps() {
        return steps;
    }

    public Integer getSleepScore() {
        return sleepScore;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public Integer getActivityMinutes() {
        return activityMinutes;
    }
}
