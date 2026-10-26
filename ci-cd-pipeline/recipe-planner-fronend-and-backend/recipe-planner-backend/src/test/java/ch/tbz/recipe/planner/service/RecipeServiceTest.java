package ch.tbz.recipe.planner.service;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import ch.tbz.recipe.planner.mapper.RecipeEntityMapper;
import ch.tbz.recipe.planner.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeEntityMapper mapper;

    @Mock
    private RecipeRepository repository;

    @InjectMocks
    private RecipeService service;

    private UUID recipeId;
    private Recipe recipe;
    private RecipeEntity entity;

    @BeforeEach
    void setUp() {
        recipeId = UUID.randomUUID();
        Ingredient ingredient = new Ingredient(null, "Sugar", "sweet", Unit.GRAMM, 100);
        recipe = new Recipe(null, "Cake", "Delicious cake", "http://example.com/cake.jpg", List.of(ingredient));
        entity = new RecipeEntity(recipeId, "Cake", "Delicious cake", "http://example.com/cake.jpg", List.of());
    }

    @Test
    void updateRecipe_withExistingRecipe_shouldReturnUpdatedRecipe() {
        when(repository.findById(recipeId)).thenReturn(Optional.of(entity));
        when(mapper.domainToEntity(any(Recipe.class))).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.entityToDomain(entity)).thenReturn(recipe);

        Optional<Recipe> result = service.updateRecipe(recipeId, recipe);

        assertThat(result).containsSame(recipe);
        assertThat(recipe.getId()).isEqualTo(recipeId);
        verify(repository).save(entity);
    }

    @Test
    void updateRecipe_withUnknownId_shouldReturnEmpty() {
        when(repository.findById(recipeId)).thenReturn(Optional.empty());

        Optional<Recipe> result = service.updateRecipe(recipeId, recipe);

        assertThat(result).isEmpty();
        verifyNoInteractions(mapper);
    }
}
