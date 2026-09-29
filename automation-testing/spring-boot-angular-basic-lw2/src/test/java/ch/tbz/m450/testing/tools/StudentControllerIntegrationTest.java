package ch.tbz.m450.testing.tools;

import ch.tbz.m450.testing.tools.repository.StudentRepository;
import ch.tbz.m450.testing.tools.repository.entities.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        studentRepository.save(new Student("Alice", "alice@tbz.ch"));
        studentRepository.save(new Student("Bob", "bob@tbz.ch"));
    }

    @Test
    @DisplayName("GET /students returns list of students with HTTP 200 OK")
    void shouldReturnAllStudents() throws Exception {
        mockMvc.perform(get("/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice")))
                .andExpect(jsonPath("$[0].email", is("alice@tbz.ch")))
                .andExpect(jsonPath("$[1].name", is("Bob")))
                .andExpect(jsonPath("$[1].email", is("bob@tbz.ch")));
    }

    @Test
    @DisplayName("POST /students saves a new student and returns HTTP 201 Created")
    void shouldCreateNewStudent() throws Exception {
        Student newStudent = new Student("Charlie", "charlie@tbz.ch", "Mediamatics");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newStudent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Charlie")))
                .andExpect(jsonPath("$.email", is("charlie@tbz.ch")))
                .andExpect(jsonPath("$.course", is("Mediamatics")));

        Iterable<Student> students = studentRepository.findAll();
        long count = 0;
        boolean found = false;
        for (Student s : students) {
            count++;
            if ("Charlie".equals(s.getName()) && "charlie@tbz.ch".equals(s.getEmail())) {
                found = true;
                assertNotNull(s.getId());
                assertEquals("Mediamatics", s.getCourse());
            }
        }
        assertEquals(3, count);
        assertEquals(true, found);
    }

    @Test
    @DisplayName("POST /students with invalid email returns HTTP 400 Bad Request")
    void shouldRejectInvalidEmail() throws Exception {
        Student invalidStudent = new Student("Dave", "not-a-valid-email", "Informatik");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.errors.email", notNullValue()));
    }

    @Test
    @DisplayName("POST /students with empty name returns HTTP 400 Bad Request")
    void shouldRejectEmptyName() throws Exception {
        Student invalidStudent = new Student("", "valid@tbz.ch", "Informatik");

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStudent)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.errors.name", notNullValue()));
    }

    @Test
    @DisplayName("CORS header verification on /students endpoint")
    void shouldAllowCorsFromAngularDevServer() throws Exception {
        mockMvc.perform(get("/students")
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}
