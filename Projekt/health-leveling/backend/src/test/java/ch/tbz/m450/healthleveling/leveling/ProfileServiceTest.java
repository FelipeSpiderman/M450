package ch.tbz.m450.healthleveling.leveling;

import ch.tbz.m450.healthleveling.day.DayDetail;
import ch.tbz.m450.healthleveling.day.DayService;
import ch.tbz.m450.healthleveling.entry.EntryResponse;
import ch.tbz.m450.healthleveling.entry.EntryType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    private static final LocalDate TODAY = LocalDate.now();

    @Mock
    private DayService dayService;

    private ProfileService service;

    @BeforeEach
    void setUp() {
        // only DayService is mocked, the calculators are real
        service = new ProfileService(dayService, new LevelCalculator(), new StreakCalculator());
    }

    @Test
    void getProfile_withoutData_isLevelOne() {
        when(dayService.getDays()).thenReturn(List.of());

        ProfileResponse profile = service.getProfile();

        assertThat(profile.level()).isEqualTo(1);
        assertThat(profile.totalXp()).isZero();
        assertThat(profile.currentStreak()).isZero();
    }

    @Test
    void getProfile_sumsXpOfAllDays() {
        when(dayService.getDays()).thenReturn(List.of(day(TODAY, null, 80), day(TODAY.minusDays(1), null, 70)));

        ProfileResponse profile = service.getProfile();

        assertThat(profile.totalXp()).isEqualTo(150);
        assertThat(profile.level()).isEqualTo(2);
        assertThat(profile.progressPercent()).isEqualTo(25);
    }

    @Test
    void getProfile_totalXpNeverNegative() {
        when(dayService.getDays()).thenReturn(List.of(day(TODAY, null, -10)));

        assertThat(service.getProfile().totalXp()).isZero();
    }

    @Test
    void getProfile_sevenDayStepStreak_addsBonusAndShowsStreak() {
        when(dayService.getDays()).thenReturn(stepGoalDays(7));

        ProfileResponse profile = service.getProfile();

        // 7 days with 0 XP each + 50 streak bonus
        assertThat(profile.totalXp()).isEqualTo(50);
        assertThat(profile.currentStreak()).isEqualTo(7);
    }

    @Test
    void getAchievements_withoutData_allLocked() {
        when(dayService.getDays()).thenReturn(List.of());

        assertThat(service.getAchievements())
                .hasSize(Achievement.values().length)
                .noneMatch(AchievementStatus::unlocked);
    }

    @Test
    void getAchievements_unlocksMatchingAchievements() {
        DayDetail fullDay = new DayDetail(TODAY, 10000, 92, null, null, List.of(entry()), 0, 0,
                DayReview.FULL_DAY_BONUS, 0, 0);
        when(dayService.getDays()).thenReturn(List.of(fullDay));

        assertThat(service.getAchievements())
                .filteredOn(AchievementStatus::unlocked)
                .extracting(AchievementStatus::achievement)
                .containsExactlyInAnyOrder(Achievement.FIRST_ENTRY, Achievement.STEP_GOAL,
                        Achievement.GOOD_SLEEP, Achievement.FULL_DAY);
    }

    @Test
    void getAchievements_weekStreakAndLevelFive() {
        List<DayDetail> days = stepGoalDays(7);
        days.add(day(TODAY.minusDays(30), null, 1000));
        when(dayService.getDays()).thenReturn(days);

        assertThat(service.getAchievements())
                .filteredOn(AchievementStatus::unlocked)
                .extracting(AchievementStatus::achievement)
                .contains(Achievement.WEEK_STREAK, Achievement.LEVEL_5);
    }

    private List<DayDetail> stepGoalDays(int count) {
        List<DayDetail> days = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            days.add(day(TODAY.minusDays(i), XpCalculator.STEP_GOAL, 0));
        }
        return days;
    }

    private DayDetail day(LocalDate date, Integer steps, int totalXp) {
        return new DayDetail(date, steps, null, null, null, List.of(), 0, 0, 0, 0, totalXp);
    }

    private EntryResponse entry() {
        return new EntryResponse(1L, EntryType.WEIGHT, TODAY.atStartOfDay(), null, null, null, null, 70.0, 5);
    }
}
