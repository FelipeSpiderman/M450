package ch.tbz.m450.healthleveling.google;

import ch.tbz.m450.healthleveling.day.DayService;
import ch.tbz.m450.healthleveling.day.ImportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);
    private static final int MAX_DAYS = 90;

    private final HealthDataClient client;
    private final DayService dayService;

    public SyncService(HealthDataClient client, DayService dayService) {
        this.client = client;
        this.dayService = dayService;
    }

    public ImportResult sync(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from must not be after to");
        }
        // Google allows max. 90 days per rollUp request
        if (ChronoUnit.DAYS.between(from, to) >= MAX_DAYS) {
            throw new IllegalArgumentException("Max. " + MAX_DAYS + " days can be synced at once");
        }
        return dayService.saveAll(client.fetchDays(from, to));
    }

    /** Keeps the data up to date automatically. Runs every 3 hours unless configured otherwise. */
    @Scheduled(cron = "${google.health.sync-cron:0 0 */3 * * *}")
    public void scheduledSync() {
        LocalDate today = LocalDate.now();
        try {
            ImportResult result = sync(today.minusDays(6), today);
            log.info("Scheduled sync done: {} days created, {} updated", result.created(), result.updated());
        } catch (HealthApiException e) {
            log.info("Scheduled sync skipped: {}", e.getMessage());
        }
    }
}
