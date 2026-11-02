package ch.tbz.m450.healthleveling.entry;

import ch.tbz.m450.healthleveling.leveling.XpCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntryServiceTest {

    private static final LocalDateTime TIME = LocalDateTime.of(2026, 10, 6, 12, 0);

    @Mock
    private EntryRepository repository;

    private EntryService service;

    @BeforeEach
    void setUp() {
        service = new EntryService(repository, new XpCalculator());
    }

    @Test
    void create_activity_savesAndReturnsXp() {
        when(repository.save(any(Entry.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EntryResponse response = service.create(
                new EntryRequest(EntryType.ACTIVITY, TIME, "Outdoor Run", 30, 250, null, null));

        assertThat(response.name()).isEqualTo("Outdoor Run");
        assertThat(response.xp()).isEqualTo(40);
    }

    @Test
    void create_activityWithoutName_isRejected() {
        assertRejected(new EntryRequest(EntryType.ACTIVITY, TIME, " ", 30, null, null, null), "name");
    }

    @Test
    void create_activityWithoutDuration_isRejected() {
        assertRejected(new EntryRequest(EntryType.ACTIVITY, TIME, "Run", null, null, null, null), "durationMinutes");
    }

    @Test
    void create_mealWithoutCalories_isRejected() {
        assertRejected(new EntryRequest(EntryType.MEAL, TIME, "Pasta", null, null, null, null), "calories");
    }

    @Test
    void create_drinkWithoutAmount_isRejected() {
        assertRejected(new EntryRequest(EntryType.DRINK, TIME, "Water", null, null, null, null), "amountMl");
    }

    @Test
    void create_weightWithoutValue_isRejected() {
        assertRejected(new EntryRequest(EntryType.WEIGHT, TIME, null, null, null, null, null), "weightKg");
    }

    @Test
    void findByDate_mapsEntriesWithXp() {
        Entry drink = new Entry(EntryType.DRINK, TIME, "Water", null, null, 500, null);
        when(repository.findByDate(LocalDate.of(2026, 10, 6))).thenReturn(List.of(drink));

        assertThat(service.findByDate(LocalDate.of(2026, 10, 6)))
                .singleElement().satisfies(entry -> assertThat(entry.xp()).isEqualTo(10));
    }

    @Test
    void delete_existingEntry_deletes() {
        when(repository.existsById(5L)).thenReturn(true);

        service.delete(5L);

        verify(repository).deleteById(5L);
    }

    @Test
    void delete_missingEntry_throwsNotFound() {
        when(repository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(5L)).isInstanceOf(ResponseStatusException.class);
        verify(repository, never()).deleteById(any());
    }

    private void assertRejected(EntryRequest request, String expectedField) {
        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedField);
        verify(repository, never()).save(any());
    }
}
