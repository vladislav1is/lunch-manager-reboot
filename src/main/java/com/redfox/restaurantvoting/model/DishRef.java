package com.redfox.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Range;

import javax.persistence.*;

@Entity
@Table(name = "dish_ref", uniqueConstraints = {@UniqueConstraint(columnNames = {"restaurant_id", "name"}, name = "dish_ref_uk")})
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = "restaurant")
public class DishRef extends NamedEntity {
    @Column(name = "price", nullable = false)
    @Range(min = 1, max = 1000_000)
    private int price;

    @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;

    @Column(name = "restaurant_id", insertable = false, updatable = false)
    private int restaurantId;

    public DishRef(Integer id, String name, int price, Restaurant restaurant) {
        super(id, name);
        this.price = price;
        this.restaurant = restaurant;
        this.restaurantId = restaurant.id();
    }
}
