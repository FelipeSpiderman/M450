package ch.tbz.m450.healthleveling.google;

import ch.tbz.m450.healthleveling.day.DailyValues;

import java.time.LocalDate;
import java.util.List;

/** Source for health data. Hides the Google API so it can be mocked or replaced. */
public interface HealthDataClient {

    List<DailyValues> fetchDays(LocalDate from, LocalDate to);
}
