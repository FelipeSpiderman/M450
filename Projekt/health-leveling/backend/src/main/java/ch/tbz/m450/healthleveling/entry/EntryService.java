package ch.tbz.m450.healthleveling.entry;

import ch.tbz.m450.healthleveling.leveling.XpCalculator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class EntryService {

    private final EntryRepository repository;
    private final XpCalculator xpCalculator;

    public EntryService(EntryRepository repository, XpCalculator xpCalculator) {
        this.repository = repository;
        this.xpCalculator = xpCalculator;
    }

    public EntryResponse create(EntryRequest request) {
        switch (request.type()) {
            case ACTIVITY -> {
                if (request.name() == null || request.name().isBlank()) {
                    throw new IllegalArgumentException("name is required for ACTIVITY");
                }
                require("durationMinutes", request.durationMinutes(), request.type());
            }
            case MEAL -> require("calories", request.calories(), request.type());
            case DRINK -> require("amountMl", request.amountMl(), request.type());
            case WEIGHT -> require("weightKg", request.weightKg(), request.type());
        }
        Entry entry = repository.save(new Entry(request.type(), request.timestamp(), request.name(),
                request.durationMinutes(), request.calories(), request.amountMl(), request.weightKg()));
        return toResponse(entry);
    }

    public List<EntryResponse> findByDate(LocalDate date) {
        return repository.findByDate(date).stream().map(this::toResponse).toList();
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry " + id + " not found");
        }
        repository.deleteById(id);
    }

    private EntryResponse toResponse(Entry entry) {
        return EntryResponse.from(entry, xpCalculator.entryXp(entry));
    }

    private void require(String field, Object value, EntryType type) {
        if (value == null) {
            throw new IllegalArgumentException(field + " is required for " + type);
        }
    }
}
