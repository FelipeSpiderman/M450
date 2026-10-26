package ch.tbz.m450.healthleveling.entry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query("select e from Entry e where e.timestamp >= :from and e.timestamp < :to order by e.timestamp")
    List<Entry> findBetween(LocalDateTime from, LocalDateTime to);

    default List<Entry> findByDate(LocalDate date) {
        return findBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }
}
