package ch.tbz.m450.healthleveling.leveling;

import ch.tbz.m450.healthleveling.day.DayService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** The profile is not stored: level and XP are always calculated from the saved days. */
@RestController
public class ProfileController {

    private final DayService dayService;
    private final LevelCalculator levelCalculator;

    public ProfileController(DayService dayService, LevelCalculator levelCalculator) {
        this.dayService = dayService;
        this.levelCalculator = levelCalculator;
    }

    @GetMapping("/api/profile")
    public LevelProgress getProfile() {
        return levelCalculator.progress(dayService.totalXp());
    }
}
