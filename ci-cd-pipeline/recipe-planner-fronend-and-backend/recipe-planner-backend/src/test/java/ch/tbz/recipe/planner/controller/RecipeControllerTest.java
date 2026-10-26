package ch.tbz.recipe.planner.controller;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RecipeService recipeService;

    @Mock
    private RecipeEntityMapper recipeEntityMapper;

    @InjectMocks
    private RecipeController recipeController;

    private Recipe sampleRecipe;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(recipeController).build();

        sampleId = UUID.randomUUID();
        Ingredient ingredient = new Ingredient(UUID.randomUUID(), "Sugar", "sweet", Unit.GRAMM, 100);
        sampleRecipe = new Recipe(sampleId, "Cake", "Delicious cake", "http://example.com/cake.jpg", List.of(ingredient));
    }

    @Test
    void getRecipes_shouldReturnListOfRecipes() throws Exception {
        when(recipeService.getRecipes()).thenReturn(List.of(sampleRecipe));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(sampleId.toString()))
                .andExpect(jsonPath("$[0].name").value("Cake"))
                .andExpect(jsonPath("$[0].description").value("Delicious cake"))
                .andExpect(jsonPath("$[0].imageUrl").value("http://example.com/cake.jpg"))
                .andExpect(jsonPath("$[0].ingredients[0].name").value("Sugar"))
                .andExpect(jsonPath("$[0].ingredients[0].amount").value(100))
                .andExpect(jsonPath("$[0].ingredients[0].unit").value("GRAMM"));

        verify(recipeService).getRecipes();
    }

    @Test
    void getRecipe_withValidId_shouldReturnRecipe() throws Exception {
        when(recipeService.getRecipeById(sampleId)).thenReturn(sampleRecipe);

        mockMvc.perform(get("/api/recipes/recipe/{recipeId}", sampleId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.name").value("Cake"))
                .andExpect(jsonPath("$.description").value("Delicious cake"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/cake.jpg"))
                .andExpect(jsonPath("$.ingredients[0].name").value("Sugar"));

        verify(recipeService).getRecipeById(sampleId);
    }

    @Test
    void addRecipe_withValidBody_shouldReturnCreatedRecipe() throws Exception {
        when(recipeService.addRecipe(any(Recipe.class))).thenReturn(sampleRecipe);

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRecipe)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.name").value("Cake"))
                .andExpect(jsonPath("$.description").value("Delicious cake"));

        verify(recipeService).addRecipe(any(Recipe.class));
    }

    @Test
    void updateRecipe_withValidBody_shouldReturnUpdatedRecipe() throws Exception {
        when(recipeService.updateRecipe(eq(sampleId), any(Recipe.class))).thenReturn(sampleRecipe);

        mockMvc.perform(put("/api/recipes/{recipeId}", sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRecipe)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(sampleId.toString()))
                .andExpect(jsonPath("$.name").value("Cake"))
                .andExpect(jsonPath("$.ingredients[0].name").value("Sugar"));

        verify(recipeService).updateRecipe(eq(sampleId), any(Recipe.class));
    }

    @Test
    void updateRecipe_withUnknownId_shouldReturnNotFound() throws Exception {
        when(recipeService.updateRecipe(eq(sampleId), any(Recipe.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        mockMvc.perform(put("/api/recipes/{recipeId}", sampleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRecipe)))
                .andExpect(status().isNotFound());

        verify(recipeService).updateRecipe(eq(sampleId), any(Recipe.class));
    }
}
