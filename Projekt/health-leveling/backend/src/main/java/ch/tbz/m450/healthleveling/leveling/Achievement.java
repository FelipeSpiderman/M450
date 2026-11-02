package ch.tbz.m450.healthleveling.leveling;

public enum Achievement {
    FIRST_ENTRY("Erster Schritt", "Erfasse deinen ersten Eintrag"),
    STEP_GOAL("10k-Club", "Erreiche 10'000 Schritte an einem Tag"),
    GOOD_SLEEP("Dornröschen", "Erreiche einen Schlaf-Score von 90 oder mehr"),
    FULL_DAY("Den ganzen Tag", "Erfasse Einträge am Morgen, am Mittag und am Abend"),
    WEEK_STREAK("Feuer und Flamme", "Erreiche das Schrittziel 7 Tage in Folge"),
    LEVEL_5("Aufsteiger", "Erreiche Level 5");

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
