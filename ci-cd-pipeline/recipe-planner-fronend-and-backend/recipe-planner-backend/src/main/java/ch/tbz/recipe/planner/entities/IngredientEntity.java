package ch.tbz.recipe.planner.entities;

import ch.tbz.recipe.planner.domain.Unit;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="INGREDIENTS")
public class IngredientEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String name;
    private String comment;
    @Enumerated(EnumType.STRING)
    private Unit unit;
    private int amount;

    public IngredientEntity() {
    }

    public IngredientEntity(UUID id, String name, String comment, Unit unit, int amount) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.unit = unit;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientEntity that = (IngredientEntity) o;
        return amount == that.amount && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(comment, that.comment) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, comment, unit, amount);
    }

    @Override
    public String toString() {
        return "IngredientEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", unit=" + unit +
                ", amount=" + amount +
                '}';
    }
}
