package ch.tbz.m450.healthleveling.leveling;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/api/profile")
    public ProfileResponse getProfile() {
        return profileService.getProfile();
    }

    @GetMapping("/api/achievements")
    public List<AchievementStatus> getAchievements() {
        return profileService.getAchievements();
    }
}
