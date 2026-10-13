package ch.tbz.recipe.planner.mapper;

import ch.tbz.recipe.planner.domain.Ingredient;
import ch.tbz.recipe.planner.domain.Unit;
import ch.tbz.recipe.planner.entities.IngredientEntity;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class IngredientEntityMapperTest {

    private final IngredientEntityMapper mapper = Mappers.getMapper(IngredientEntityMapper.class);

    @Test
    void entityToDomain_shouldMapAllFieldsCorrectly_usingSoftAssertions() {
        UUID id = UUID.randomUUID();
        IngredientEntity entity = new IngredientEntity(id, "Flour", "organic", Unit.GRAMM, 500);

        Ingredient domain = mapper.entityToDomain(entity);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(domain).isNotNull();
        softly.assertThat(domain.getId()).isEqualTo(entity.getId());
        softly.assertThat(domain.getName()).isEqualTo(entity.getName());
        softly.assertThat(domain.getComment()).isEqualTo(entity.getComment());
        softly.assertThat(domain.getUnit()).isEqualTo(entity.getUnit());
        softly.assertThat(domain.getAmount()).isEqualTo(entity.getAmount());
        softly.assertAll();
    }

    @Test
    void entityToDomain_nullInput_shouldReturnNull() {
        Ingredient domain = mapper.entityToDomain(null);
        assertThat(domain).isNull();
    }

    @Test
    void domainToEntity_shouldMapAllFieldsCorrectly_usingSoftAssertions() {
        UUID id = UUID.randomUUID();
        Ingredient domain = new Ingredient(id, "Milk", "whole milk", Unit.DECILITRE, 2);

        IngredientEntity entity = mapper.domainToEntity(domain);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entity).isNotNull();
            softly.assertThat(entity.getId()).isEqualTo(domain.getId());
            softly.assertThat(entity.getName()).isEqualTo(domain.getName());
            softly.assertThat(entity.getComment()).isEqualTo(domain.getComment());
            softly.assertThat(entity.getUnit()).isEqualTo(domain.getUnit());
            softly.assertThat(entity.getAmount()).isEqualTo(domain.getAmount());
        });
    }

    @Test
    void domainToEntity_nullInput_shouldReturnNull() {
        IngredientEntity entity = mapper.domainToEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    void entitiesToDomains_shouldMapListCorrectly_usingSoftAssertions() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        IngredientEntity entity1 = new IngredientEntity(id1, "Egg", "free range", Unit.PIECE, 3);
        IngredientEntity entity2 = new IngredientEntity(id2, "Water", "tap", Unit.LITRE, 1);

        List<Ingredient> domains = mapper.entitiesToDomains(List.of(entity1, entity2));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(domains).isNotNull();
            softly.assertThat(domains).hasSize(2);
            softly.assertThat(domains.get(0).getId()).isEqualTo(id1);
            softly.assertThat(domains.get(0).getName()).isEqualTo("Egg");
            softly.assertThat(domains.get(0).getComment()).isEqualTo("free range");
            softly.assertThat(domains.get(0).getUnit()).isEqualTo(Unit.PIECE);
            softly.assertThat(domains.get(0).getAmount()).isEqualTo(3);

            softly.assertThat(domains.get(1).getId()).isEqualTo(id2);
            softly.assertThat(domains.get(1).getName()).isEqualTo("Water");
            softly.assertThat(domains.get(1).getComment()).isEqualTo("tap");
            softly.assertThat(domains.get(1).getUnit()).isEqualTo(Unit.LITRE);
            softly.assertThat(domains.get(1).getAmount()).isEqualTo(1);
        });
    }

    @Test
    void domainsToEntities_shouldMapListCorrectly_usingSoftAssertions() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Ingredient domain1 = new Ingredient(id1, "Sugar", "fine", Unit.GRAMM, 200);
        Ingredient domain2 = new Ingredient(id2, "Butter", "salted", Unit.GRAMM, 150);

        List<IngredientEntity> entities = mapper.domainsToEntities(List.of(domain1, domain2));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(entities).isNotNull();
            softly.assertThat(entities).hasSize(2);
            softly.assertThat(entities.get(0).getId()).isEqualTo(id1);
            softly.assertThat(entities.get(0).getName()).isEqualTo("Sugar");
            softly.assertThat(entities.get(0).getComment()).isEqualTo("fine");
            softly.assertThat(entities.get(0).getUnit()).isEqualTo(Unit.GRAMM);
            softly.assertThat(entities.get(0).getAmount()).isEqualTo(200);

            softly.assertThat(entities.get(1).getId()).isEqualTo(id2);
            softly.assertThat(entities.get(1).getName()).isEqualTo("Butter");
            softly.assertThat(entities.get(1).getComment()).isEqualTo("salted");
            softly.assertThat(entities.get(1).getUnit()).isEqualTo(Unit.GRAMM);
            softly.assertThat(entities.get(1).getAmount()).isEqualTo(150);
        });
    }
}
