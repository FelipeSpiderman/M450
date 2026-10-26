package ch.tbz.m450.healthleveling.google;

import ch.tbz.m450.healthleveling.day.DailyValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GoogleHealthClientTest {

    private static final LocalDate FROM = LocalDate.of(2026, 10, 4);
    private static final LocalDate TO = LocalDate.of(2026, 10, 6);

    private MockRestServiceServer server;
    private GoogleHealthClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new GoogleHealthClient(builder, "id", "secret", "refresh");
    }

    @Test
    void fetchDays_combinesStepsAndMainSleepPerDay() throws IOException {
        expectToken();
        server.expect(requestTo(GoogleHealthClient.BASE_URL + "/steps/dataPoints:dailyRollUp"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer test-access-token"))
                .andExpect(jsonPath("$.range.start.date.day").value(4))
                .andExpect(jsonPath("$.range.end.date.day").value(7))
                .andRespond(json("steps-daily.json"));
        server.expect(requestTo(startsWith(GoogleHealthClient.BASE_URL + "/sleep/dataPoints:reconcile")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(json("sleep-page1.json"));
        server.expect(requestTo(containsString("pageToken=page-2")))
                .andRespond(json("sleep-page2.json"));

        List<DailyValues> days = client.fetchDays(FROM, TO);

        // 432 min of 480 = 90, 540 min is capped at 100, the nap is ignored
        assertThat(days).containsExactly(
                new DailyValues(LocalDate.of(2026, 10, 5), 4810, 100, null, null),
                new DailyValues(LocalDate.of(2026, 10, 6), 12034, 90, null, null));
        server.verify();
    }

    @Test
    void fetchDays_sleepEndingAfterMidnightLocalTime_countsForNextDay() throws IOException {
        // ends 23:30 UTC = 01:30 local time (+02:00) on 2026-10-06
        expectToken();
        server.expect(requestTo(GoogleHealthClient.BASE_URL + "/steps/dataPoints:dailyRollUp"))
                .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        server.expect(requestTo(startsWith(GoogleHealthClient.BASE_URL + "/sleep/dataPoints:reconcile")))
                .andRespond(json("sleep-after-midnight.json"));

        List<DailyValues> days = client.fetchDays(FROM, TO);

        assertThat(days).containsExactly(new DailyValues(LocalDate.of(2026, 10, 6), null, 50, null, null));
    }

    @Test
    void fetchDays_tokenRequestFails_throwsHealthApiException() {
        server.expect(requestTo(GoogleHealthClient.TOKEN_URL))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> client.fetchDays(FROM, TO))
                .isInstanceOf(HealthApiException.class);
    }

    @Test
    void fetchDays_apiError_throwsHealthApiException() throws IOException {
        expectToken();
        server.expect(requestTo(GoogleHealthClient.BASE_URL + "/steps/dataPoints:dailyRollUp"))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        assertThatThrownBy(() -> client.fetchDays(FROM, TO))
                .isInstanceOf(HealthApiException.class);
    }

    private void expectToken() throws IOException {
        server.expect(requestTo(GoogleHealthClient.TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(containsString("grant_type=refresh_token")))
                .andExpect(content().string(containsString("refresh_token=refresh")))
                .andRespond(json("token.json"));
    }

    private static org.springframework.test.web.client.ResponseCreator json(String fileName) throws IOException {
        try (InputStream in = GoogleHealthClientTest.class.getResourceAsStream("/google/" + fileName)) {
            return withSuccess(new String(in.readAllBytes(), StandardCharsets.UTF_8), MediaType.APPLICATION_JSON);
        }
    }
}
