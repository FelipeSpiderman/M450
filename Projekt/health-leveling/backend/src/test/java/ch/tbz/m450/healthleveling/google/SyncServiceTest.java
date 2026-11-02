package ch.tbz.m450.healthleveling.google;

import ch.tbz.m450.healthleveling.day.DailyValues;
import ch.tbz.m450.healthleveling.day.DayService;
import ch.tbz.m450.healthleveling.day.ImportResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SyncServiceTest {

    private static final LocalDate FROM = LocalDate.of(2026, 10, 1);
    private static final LocalDate TO = LocalDate.of(2026, 10, 7);

    @Mock
    private HealthDataClient client;

    @Mock
    private DayService dayService;

    @InjectMocks
    private SyncService service;

    @Test
    void sync_savesEveryDayFromTheClient() {
        DailyValues day1 = new DailyValues(FROM, 5000, 80, null, null);
        DailyValues day2 = new DailyValues(TO, 12000, null, null, null);
        when(client.fetchDays(FROM, TO)).thenReturn(List.of(day1, day2));
        when(dayService.saveAll(List.of(day1, day2))).thenReturn(new ImportResult(2, 0));

        ImportResult result = service.sync(FROM, TO);

        assertThat(result).isEqualTo(new ImportResult(2, 0));
    }

    @Test
    void sync_fromAfterTo_isRejected() {
        assertThatThrownBy(() -> service.sync(TO, FROM))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(client);
    }

    @Test
    void sync_moreThan90Days_isRejected() {
        assertThatThrownBy(() -> service.sync(FROM, FROM.plusDays(90)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("90");
        verifyNoInteractions(client);
    }

    @Test
    void sync_clientUnavailable_passesErrorOn() {
        when(client.fetchDays(FROM, TO)).thenThrow(new HealthApiException("not configured"));

        assertThatThrownBy(() -> service.sync(FROM, TO))
                .isInstanceOf(HealthApiException.class);
        verifyNoInteractions(dayService);
    }

    @Test
    void scheduledSync_syncsTheLastSevenDays() {
        LocalDate today = LocalDate.now();
        when(client.fetchDays(today.minusDays(6), today)).thenReturn(List.of());
        when(dayService.saveAll(List.of())).thenReturn(new ImportResult(0, 0));

        service.scheduledSync();

        verify(dayService).saveAll(List.of());
    }

    @Test
    void scheduledSync_apiNotAvailable_doesNotThrow() {
        LocalDate today = LocalDate.now();
        when(client.fetchDays(today.minusDays(6), today)).thenThrow(new HealthApiException("not configured"));

        assertThatNoException().isThrownBy(() -> service.scheduledSync());
    }
}
