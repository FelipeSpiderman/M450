package ch.tbz.m450.healthleveling.day;

import ch.tbz.m450.healthleveling.entry.Entry;
import ch.tbz.m450.healthleveling.entry.EntryRepository;
import ch.tbz.m450.healthleveling.entry.EntryResponse;
import ch.tbz.m450.healthleveling.leveling.DayReview;
import ch.tbz.m450.healthleveling.leveling.XpCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DayService {

    private final DailyRecordRepository recordRepository;
    private final EntryRepository entryRepository;
    private final XpCalculator xpCalculator;
    private final DayReview dayReview;

    public DayService(DailyRecordRepository recordRepository, EntryRepository entryRepository,
                      XpCalculator xpCalculator, DayReview dayReview) {
        this.recordRepository = recordRepository;
        this.entryRepository = entryRepository;
        this.xpCalculator = xpCalculator;
        this.dayReview = dayReview;
    }

    /** Creates new days or updates existing ones (used by manual input, import and sync). */
    @Transactional
    public ImportResult saveAll(List<DailyValues> days) {
        int created = 0;
        for (DailyValues values : days) {
            DailyRecord record = recordRepository.findByDate(values.date()).orElse(null);
            if (record == null) {
                record = new DailyRecord(values.date());
                created++;
            }
            record.update(values);
            recordRepository.save(record);
        }
        return new ImportResult(created, days.size() - created);
    }

    public DayDetail getDay(LocalDate date) {
        return buildDay(date, recordRepository.findByDate(date).orElse(null), entryRepository.findByDate(date));
    }

    /** All days that have values or entries, newest first. */
    public List<DayDetail> getDays() {
        Map<LocalDate, DailyRecord> records = recordRepository.findAll().stream()
                .collect(Collectors.toMap(DailyRecord::getDate, Function.identity()));
        Map<LocalDate, List<Entry>> entries = entryRepository.findAll().stream()
                .collect(Collectors.groupingBy(entry -> entry.getTimestamp().toLocalDate()));

        TreeSet<LocalDate> dates = new TreeSet<>(Comparator.reverseOrder());
        dates.addAll(records.keySet());
        dates.addAll(entries.keySet());

        return dates.stream()
                .map(date -> buildDay(date, records.get(date), entries.getOrDefault(date, List.of())))
                .toList();
    }

    private DayDetail buildDay(LocalDate date, DailyRecord record, List<Entry> entries) {
        // an empty record keeps the code below free of null checks
        DailyRecord values = record != null ? record : new DailyRecord(date);
        List<EntryResponse> entryResponses = entries.stream()
                .map(entry -> EntryResponse.from(entry, xpCalculator.entryXp(entry)))
                .toList();

        int valuesXp = xpCalculator.dailyRecordXp(values);
        int entriesXp = entryResponses.stream().mapToInt(EntryResponse::xp).sum();
        int slotBonus = dayReview.slotBonus(entries);
        int caloriePenalty = dayReview.caloriePenalty(entries);

        return new DayDetail(date, values.getSteps(), values.getSleepScore(), values.getActivityCount(),
                values.getActivityMinutes(), entryResponses, valuesXp, entriesXp, slotBonus, caloriePenalty,
                valuesXp + entriesXp + slotBonus + caloriePenalty);
    }
}
