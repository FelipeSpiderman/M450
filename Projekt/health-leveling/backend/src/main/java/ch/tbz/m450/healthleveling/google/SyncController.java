package ch.tbz.m450.healthleveling.google;

import ch.tbz.m450.healthleveling.day.ImportResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    /** Without parameters the last 7 days (including today) are synced. */
    @PostMapping("/api/sync")
    public ImportResult sync(@RequestParam(required = false) LocalDate from,
                             @RequestParam(required = false) LocalDate to) {
        LocalDate end = to != null ? to : LocalDate.now();
        LocalDate start = from != null ? from : end.minusDays(6);
        return syncService.sync(start, end);
    }
}
