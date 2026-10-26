package ch.tbz.m450.healthleveling.google;

import ch.tbz.m450.healthleveling.day.DailyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/** Reads steps and sleep from the Google Health API (see Projekt/ENDPOINTS.md). */
@Component
public class GoogleHealthClient implements HealthDataClient {

    static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    static final String BASE_URL = "https://health.googleapis.com/v4/users/me/dataTypes";

    private static final int SLEEP_GOAL_MINUTES = 480;
    private static final int MAX_PAGES = 10;

    private final RestClient restClient;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;

    @Autowired
    public GoogleHealthClient(@Value("${google.health.client-id:}") String clientId,
                              @Value("${google.health.client-secret:}") String clientSecret,
                              @Value("${google.health.refresh-token:}") String refreshToken) {
        this(RestClient.builder(), clientId, clientSecret, refreshToken);
    }

    // used by the tests to plug in a fake server
    GoogleHealthClient(RestClient.Builder builder, String clientId, String clientSecret, String refreshToken) {
        this.restClient = builder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
    }

    @Override
    public List<DailyValues> fetchDays(LocalDate from, LocalDate to) {
        if (refreshToken.isBlank()) {
            throw new HealthApiException("Google Health API is not configured");
        }
        try {
            String token = fetchAccessToken();
            Map<LocalDate, Integer> steps = fetchSteps(token, from, to);
            Map<LocalDate, Integer> sleep = fetchSleepScores(token, from);

            TreeSet<LocalDate> dates = new TreeSet<>(steps.keySet());
            dates.addAll(sleep.keySet());
            return dates.stream()
                    .filter(date -> !date.isBefore(from) && !date.isAfter(to))
                    .map(date -> new DailyValues(date, steps.get(date), sleep.get(date), null, null))
                    .toList();
        } catch (RestClientException e) {
            throw new HealthApiException("Google Health API request failed: " + e.getMessage());
        }
    }

    // access tokens expire after 1 hour, so a new one is requested with the refresh token every time
    private String fetchAccessToken() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);
        form.add("grant_type", "refresh_token");

        JsonNode response = restClient.post().uri(TOKEN_URL).body(form).retrieve().body(JsonNode.class);
        return response.path("access_token").asString("");
    }

    private Map<LocalDate, Integer> fetchSteps(String token, LocalDate from, LocalDate to) {
        // the end date is exclusive, so +1 day to include "to"
        Map<String, Object> body = Map.of(
                "range", Map.of("start", civilDate(from), "end", civilDate(to.plusDays(1))),
                "windowSizeDays", 1);
        JsonNode response = restClient.post()
                .uri(BASE_URL + "/steps/dataPoints:dailyRollUp")
                .header("Authorization", "Bearer " + token)
                .body(body)
                .retrieve()
                .body(JsonNode.class);

        Map<LocalDate, Integer> steps = new HashMap<>();
        for (JsonNode point : response.path("rollupDataPoints")) {
            JsonNode date = point.path("civilStartTime").path("date");
            LocalDate day = LocalDate.of(date.path("year").asInt(0), date.path("month").asInt(0), date.path("day").asInt(0));
            steps.put(day, point.path("steps").path("countSum").asInt(0));
        }
        return steps;
    }

    /**
     * The API has no sleep score like the Fitbit app, so it is estimated:
     * minutes asleep compared to an 8 hour goal, max 100.
     */
    private Map<LocalDate, Integer> fetchSleepScores(String token, LocalDate from) {
        // start one day earlier: a night that starts the evening before "from" ends on "from"
        String filter = "sleep.interval.civil_start_time >= \"" + from.minusDays(1) + "T00:00:00\"";
        Map<LocalDate, Integer> scores = new HashMap<>();
        String pageToken = "";

        for (int page = 0; page < MAX_PAGES; page++) {
            JsonNode response = restClient.get()
                    .uri(sleepUri(filter, pageToken))
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(JsonNode.class);

            for (JsonNode point : response.path("dataPoints")) {
                JsonNode sleep = point.path("sleep");
                if (sleep.path("metadata").path("mainSleep").asBoolean(false)) { // naps don't count
                    int minutesAsleep = sleep.path("summary").path("minutesAsleep").asInt(0);
                    int score = Math.min(100, minutesAsleep * 100 / SLEEP_GOAL_MINUTES);
                    scores.merge(localWakeUpDate(sleep.path("interval")), score, Math::max);
                }
            }
            pageToken = response.path("nextPageToken").asString("");
            if (pageToken.isEmpty()) {
                break;
            }
        }
        return scores;
    }

    private URI sleepUri(String filter, String pageToken) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(BASE_URL + "/sleep/dataPoints:reconcile")
                .queryParam("filter", "{filter}");
        if (!pageToken.isEmpty()) {
            builder.queryParam("pageToken", "{pageToken}");
        }
        return builder.encode().buildAndExpand(filter, pageToken).toUri();
    }

    // endTime is UTC, endUtcOffset looks like "7200s" -> local date of waking up
    private LocalDate localWakeUpDate(JsonNode interval) {
        Instant end = Instant.parse(interval.path("endTime").asString());
        int offsetSeconds = Integer.parseInt(interval.path("endUtcOffset").asString("0s").replace("s", ""));
        return end.atOffset(ZoneOffset.ofTotalSeconds(offsetSeconds)).toLocalDate();
    }

    private Map<String, Object> civilDate(LocalDate date) {
        return Map.of(
                "date", Map.of("year", date.getYear(), "month", date.getMonthValue(), "day", date.getDayOfMonth()),
                "time", Map.of());
    }
}
