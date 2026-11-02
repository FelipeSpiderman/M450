package ch.tbz.m450.healthleveling.importer;

import ch.tbz.m450.healthleveling.day.DailyValues;
import ch.tbz.m450.healthleveling.day.DayService;
import ch.tbz.m450.healthleveling.day.ImportResult;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Imports daily values from a CSV or JSON file (format: see Projekt/IMPORT_FORMAT.md, section 6). */
@Service
public class ImportService {

    static final String CSV_HEADER = "date,steps,sleep_score,activity_count,activity_minutes";

    private final DayService dayService;
    private final ObjectMapper objectMapper;

    public ImportService(DayService dayService, ObjectMapper objectMapper) {
        this.dayService = dayService;
        this.objectMapper = objectMapper;
    }

    public ImportResult importFile(String fileName, String content) {
        return dayService.saveAll(parse(fileName, content));
    }

    List<DailyValues> parse(String fileName, String content) {
        String name = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
        if (!name.endsWith(".csv") && !name.endsWith(".json")) {
            throw new IllegalArgumentException("Only .csv and .json files are supported");
        }
        if (content.isBlank()) {
            throw new IllegalArgumentException("File is empty");
        }
        List<DailyValues> days = name.endsWith(".csv") ? parseCsv(content) : parseJson(content);
        if (days.isEmpty()) {
            throw new IllegalArgumentException("File contains no data");
        }
        return days;
    }

    private List<DailyValues> parseCsv(String content) {
        // strip a possible UTF-8 BOM, Excel likes to add one
        String[] lines = content.replace("﻿", "").split("\\r?\\n");
        if (!lines[0].trim().equalsIgnoreCase(CSV_HEADER)) {
            throw new IllegalArgumentException("Invalid header, expected: " + CSV_HEADER);
        }
        List<DailyValues> days = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isBlank()) {
                continue;
            }
            try {
                days.add(parseCsvLine(lines[i]));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Error on line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return days;
    }

    private DailyValues parseCsvLine(String line) {
        // -1 keeps trailing empty cells, e.g. "2026-10-06,,72,,"
        String[] cells = line.split(",", -1);
        if (cells.length != 5) {
            throw new IllegalArgumentException("expected 5 columns but got " + cells.length);
        }
        return new DailyValues(LocalDate.parse(cells[0].trim()),
                toInteger(cells[1]), toInteger(cells[2]), toInteger(cells[3]), toInteger(cells[4]));
    }

    private Integer toInteger(String cell) {
        return cell.isBlank() ? null : Integer.valueOf(cell.trim());
    }

    private List<DailyValues> parseJson(String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<List<DailyValues>>() {});
        } catch (JacksonException e) {
            // the cause holds the IllegalArgumentException from DailyValues, if that was the problem
            String reason = e.getCause() != null ? e.getCause().getMessage() : e.getOriginalMessage();
            throw new IllegalArgumentException("Invalid JSON: " + reason);
        }
    }
}
