package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Recipe;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import ch.tbz.recipe.planner.entities.RecipeEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RecipeEntityMapperTest {

    private final RecipeEntityMapper mapper = Mappers.getMapper(RecipeEntityMapper.class);

    @Test
    void entityToDomain_shouldMapAllFieldsCorrectly_usingSoftAssertions() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        IngredientEntity ingredientEntity = new IngredientEntity(ingredientId, "Flour", "white", Unit.GRAMM, 250);
        RecipeEntity recipeEntity = new RecipeEntity(recipeId, "Pancakes", "Fluffy pancakes", "http://example.com/pancakes.jpg", List.of(ingredientEntity));

        Recipe domain = mapper.entityToDomain(recipeEntity);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(domain).isNotNull();
            softly.assertThat(domain.getId()).isEqualTo(recipeEntity.getId());
            softly.assertThat(domain.getName()).isEqualTo(recipeEntity.getName());
            softly.assertThat(domain.getDescription()).isEqualTo(recipeEntity.getDescription());
            softly.assertThat(domain.getImageUrl()).isEqualTo(recipeEntity.getImageUrl());
            softly.assertThat(domain.getIngredients()).isNotNull().hasSize(1);

            Ingredient mappedIngredient = domain.getIngredients().get(0);
            softly.assertThat(mappedIngredient.getId()).isEqualTo(ingredientId);
            softly.assertThat(mappedIngredient.getName()).isEqualTo("Flour");
            softly.assertThat(mappedIngredient.getComment()).isEqualTo("white");
            softly.assertThat(mappedIngredient.getUnit()).isEqualTo(Unit.GRAMM);
            softly.assertThat(mappedIngredient.getAmount()).isEqualTo(250);
        });
    }

    @Test
    void entityToDomain_nullInput_shouldReturnNull() {
        Recipe domain = mapper.entityToDomain(null);
        assertThat(domain).isNull();
    }

    @Test
    void domainToEntity_shouldMapAllFieldsCorrectly_usingSoftAssertions() {
        UUID recipeId = UUID.randomUUID();
        UUID ingredientId = UUID.randomUUID();
        Ingredient ingredient = new Ingredient(ingredientId, "Eggs", "fresh", Unit.PIECE, 2);
        Recipe recipe = new Recipe(recipeId, "Omelette", "Quick breakfast", "http://example.com/omelette.jpg", List.of(ingredient));

        RecipeEntity entity = mapper.domainToEntity(recipe);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entity).isNotNull();
            softly.assertThat(entity.getId()).isEqualTo(recipe.getId());
            softly.assertThat(entity.getName()).isEqualTo(recipe.getName());
            softly.assertThat(entity.getDescription()).isEqualTo(recipe.getDescription());
            softly.assertThat(entity.getImageUrl()).isEqualTo(recipe.getImageUrl());
            softly.assertThat(entity.getIngredients()).isNotNull().hasSize(1);

            IngredientEntity mappedIngredient = entity.getIngredients().get(0);
            softly.assertThat(mappedIngredient.getId()).isEqualTo(ingredientId);
            softly.assertThat(mappedIngredient.getName()).isEqualTo("Eggs");
            softly.assertThat(mappedIngredient.getComment()).isEqualTo("fresh");
            softly.assertThat(mappedIngredient.getUnit()).isEqualTo(Unit.PIECE);
            softly.assertThat(mappedIngredient.getAmount()).isEqualTo(2);
        });
    }

    @Test
    void domainToEntity_nullInput_shouldReturnNull() {
        RecipeEntity entity = mapper.domainToEntity(null);
        assertThat(entity).isNull();
    }
}
