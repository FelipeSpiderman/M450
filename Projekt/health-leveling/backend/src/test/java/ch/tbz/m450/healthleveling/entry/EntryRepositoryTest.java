package ch.tbz.m450.healthleveling.entry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/** Tests the custom query against a real (H2) database. */
@DataJpaTest
class EntryRepositoryTest {

    @Autowired
    private EntryRepository repository;

    @Test
    void findByDate_returnsOnlyEntriesOfThatDaySortedByTime() {
        save(LocalDateTime.of(2026, 10, 6, 19, 0));
        save(LocalDateTime.of(2026, 10, 6, 0, 0));
        save(LocalDateTime.of(2026, 10, 5, 23, 59));
        save(LocalDateTime.of(2026, 10, 7, 0, 0));

        assertThat(repository.findByDate(LocalDate.of(2026, 10, 6)))
                .extracting(Entry::getTimestamp)
                .containsExactly(LocalDateTime.of(2026, 10, 6, 0, 0), LocalDateTime.of(2026, 10, 6, 19, 0));
    }

    private void save(LocalDateTime timestamp) {
        repository.save(new Entry(EntryType.MEAL, timestamp, null, null, 500, null, null));
    }
}
