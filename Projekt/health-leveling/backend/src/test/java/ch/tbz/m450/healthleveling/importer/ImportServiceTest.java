package ch.tbz.m450.healthleveling.importer;

import ch.tbz.m450.healthleveling.day.DailyValues;
import ch.tbz.m450.healthleveling.day.DayService;
import ch.tbz.m450.healthleveling.day.ImportResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    private static final String HEADER = ImportService.CSV_HEADER + "\n";
    private static final LocalDate DAY_1 = LocalDate.of(2026, 10, 6);
    private static final LocalDate DAY_2 = LocalDate.of(2026, 10, 7);

    @Mock
    private DayService dayService;

    private ImportService service;

    @BeforeEach
    void setUp() {
        service = new ImportService(dayService, JsonMapper.builder().build());
    }

    @Test
    void importFile_savesParsedDays() throws IOException {
        List<DailyValues> expected = List.of(
                new DailyValues(DAY_1, 8412, 72, 1, 69),
                new DailyValues(DAY_2, 2103, 74, 0, 0));
        when(dayService.saveAll(expected)).thenReturn(new ImportResult(2, 0));

        assertThat(service.importFile("export.CSV", read("valid.csv"))).isEqualTo(new ImportResult(2, 0));
    }

    @Test
    void parseCsv_blankCellsBecomeNullAndEmptyLinesAreSkipped() throws IOException {
        assertThat(service.parse("export.csv", read("blank-cells.csv"))).containsExactly(
                new DailyValues(DAY_1, null, 72, null, null),
                new DailyValues(DAY_2, 0, null, 0, 0));
    }

    @Test
    void parseCsv_windowsLineEndings_work() {
        assertThat(service.parse("export.csv", HEADER.replace("\n", "\r\n") + "2026-10-06,100,50,0,0\r\n")).hasSize(1);
    }

    @Test
    void parseJson_validFile_returnsAllDays() throws IOException {
        assertThat(service.parse("export.json", read("valid.json"))).containsExactly(
                new DailyValues(DAY_1, 8412, 72, 1, 69),
                new DailyValues(DAY_2, 2103, null, null, null));
    }

    @Test
    void parse_invalidFiles_areRejected() {
        assertRejected("export.txt", "x", "Only .csv and .json");
        assertRejected("export.csv", " ", "empty");
        assertRejected("export.csv", HEADER, "no data");
        assertRejected("export.csv", "day,steps\n2026-10-06,100", "header");
        assertRejected("export.json", "[]", "no data");
        assertRejected("export.json", "[ { \"date\": ", "Invalid JSON");
        assertRejected("export.json", "{ \"date\": \"2026-10-06\" }", "Invalid JSON");
    }

    @Test
    void parseCsv_invalidLine_isRejectedWithLineNumber() {
        assertRejected("export.csv", HEADER + "2026-10-06,abc,50,0,0", "line 2");
        assertRejected("export.csv", HEADER + "06.10.2026,100,50,0,0", "line 2");
        assertRejected("export.csv", HEADER + "2026-10-06,100,50", "5 columns");
    }

    @Test
    void parseCsv_negativeSteps_isRejectedAndNothingIsSaved() throws IOException {
        String csv = read("negative-steps.csv");

        assertThatThrownBy(() -> service.importFile("export.csv", csv))
                .hasMessageContaining("line 3")
                .hasMessageContaining("steps");
        verifyNoInteractions(dayService);
    }

    @Test
    void parseJson_invalidValue_isRejected() throws IOException {
        String json = read("invalid-score.json");

        assertThatThrownBy(() -> service.parse("export.json", json))
                .hasMessageContaining("sleepScore");
    }

    private void assertRejected(String fileName, String content, String expectedMessage) {
        assertThatThrownBy(() -> service.parse(fileName, content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(expectedMessage);
    }

    private String read(String fileName) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/import/" + fileName)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
