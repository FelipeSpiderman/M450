package ch.tbz.m450.healthleveling.leveling;

public enum Achievement {
    FIRST_ENTRY("First step", "Log your first entry"),
    STEP_GOAL("10k club", "Reach 10'000 steps in one day"),
    GOOD_SLEEP("Sleeping beauty", "Get a sleep score of 90 or more"),
    FULL_DAY("All day long", "Log entries in the morning, at midday and in the evening"),
    WEEK_STREAK("On fire", "Reach the step goal 7 days in a row"),
    LEVEL_5("Rising star", "Reach level 5");

    private final String title;
    private final String description;

    Achievement(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
