package ch.tbz.m450.healthleveling;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the whole application over HTTP: controller, service and H2 database together.
 * Every test is rolled back afterwards, so each one starts with an empty database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class HealthLevelingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void newProfile_startsAtLevelOne() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.level").value(1))
                .andExpect(jsonPath("$.totalXp").value(0))
                .andExpect(jsonPath("$.xpForNextLevel").value(100));
    }

    @Test
    void importAndEntries_levelUpTheProfile() throws Exception {
        // 2026-10-06: 40 steps + 36 sleep + 79 activities = 155 XP
        // 2026-10-07: 10 steps + 37 sleep + 0 activities  =  47 XP
        importFile("valid.csv")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(2));

        // 10 + 40 + 10 XP and +30 because all three day slots have an entry
        postEntry("""
                {"type": "MEAL", "timestamp": "2026-10-07T08:00:00", "name": "Muesli", "calories": 600}""")
                .andExpect(status().isCreated());
        postEntry("""
                {"type": "ACTIVITY", "timestamp": "2026-10-07T12:30:00", "name": "Run", "durationMinutes": 30}""")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.xp").value(40));
        postEntry("""
                {"type": "DRINK", "timestamp": "2026-10-07T19:00:00", "name": "Water", "amountMl": 500}""")
                .andExpect(status().isCreated());

        // 155 + 47 + 90 = 292 XP -> level 2 (100 to 299)
        mockMvc.perform(get("/api/profile"))
                .andExpect(jsonPath("$.totalXp").value(292))
                .andExpect(jsonPath("$.level").value(2))
                .andExpect(jsonPath("$.progressPercent").value(96));

        mockMvc.perform(get("/api/days/2026-10-07"))
                .andExpect(jsonPath("$.entries.length()").value(3))
                .andExpect(jsonPath("$.slotBonus").value(30))
                .andExpect(jsonPath("$.totalXp").value(137));

        mockMvc.perform(get("/api/entries").param("date", "2026-10-07"))
                .andExpect(jsonPath("$.length()").value(3));

        // 10'000 instead of 2'103 steps: 70 instead of 10 XP -> 352 XP -> level 3
        postDay("{\"date\": \"2026-10-07\", \"steps\": 10000}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sleepScore").value(74));

        mockMvc.perform(get("/api/profile"))
                .andExpect(jsonPath("$.totalXp").value(352))
                .andExpect(jsonPath("$.level").value(3));

        mockMvc.perform(get("/api/days"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].date").value("2026-10-07"));
    }

    @Test
    void importSameFileTwice_updatesInsteadOfDuplicating() throws Exception {
        importFile("valid.json");

        importFile("valid.json")
                .andExpect(jsonPath("$.created").value(0))
                .andExpect(jsonPath("$.updated").value(2));
        mockMvc.perform(get("/api/days")).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void invalidImport_returns400AndSavesNothing() throws Exception {
        importFile("negative-steps.csv")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("line 3")));

        mockMvc.perform(get("/api/days")).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void importWithoutFile_returns400() throws Exception {
        mockMvc.perform(multipart("/api/import")).andExpect(status().isBadRequest());
    }

    @Test
    void invalidDailyValues_return400WithReason() throws Exception {
        postDay("{\"date\": \"2026-10-07\", \"sleepScore\": 101}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("sleepScore")));

        mockMvc.perform(get("/api/days/07.10.2026")).andExpect(status().isBadRequest());
    }

    @Test
    void invalidEntries_return400WithReason() throws Exception {
        postEntry("{\"timestamp\": \"2026-10-06T12:00:00\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("type")));
        postEntry("{\"type\": \"MEAL\", \"timestamp\": \"2026-10-06T12:00:00\", \"calories\": 5001}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("calories")));
        postEntry("{\"type\": \"MEAL\", \"timestamp\": \"2026-10-06T12:00:00\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("calories is required for MEAL"));
        postEntry("{\"type\": \"WEIGHT\", \"timestamp\": \"2999-01-01T12:00:00\", \"weightKg\": 70}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value(containsString("timestamp")));
        postEntry("{\"type\": \"PIZZA\", \"timestamp\": \"2026-10-06T12:00:00\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteEntry_removesItsXp() throws Exception {
        String response = postEntry("""
                {"type": "WEIGHT", "timestamp": "2026-10-07T07:00:00", "weightKg": 72.5}""")
                .andReturn().getResponse().getContentAsString();
        String id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(delete("/api/entries/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/entries/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Entry " + id + " not found"));
        mockMvc.perform(get("/api/profile")).andExpect(jsonPath("$.totalXp").value(0));
    }

    @Test
    void sync_withoutGoogleCredentials_returns503() throws Exception {
        mockMvc.perform(post("/api/sync"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail").value("Google Health API is not configured"));
    }

    @Test
    void sync_invalidRange_returns400() throws Exception {
        mockMvc.perform(post("/api/sync").param("from", "2026-10-07").param("to", "2026-10-01"))
                .andExpect(status().isBadRequest());
    }

    private ResultActions postEntry(String json) throws Exception {
        return mockMvc.perform(post("/api/entries").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions postDay(String json) throws Exception {
        return mockMvc.perform(post("/api/days").contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions importFile(String name) throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/import/" + name)) {
            return mockMvc.perform(multipart("/api/import")
                    .file(new MockMultipartFile("file", name, "text/plain", in.readAllBytes())));
        }
    }
}
