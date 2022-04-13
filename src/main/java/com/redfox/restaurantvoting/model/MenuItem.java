package com.redfox.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "menu_item", uniqueConstraints = {@UniqueConstraint(columnNames = {"actual_date", "dish_ref_id"}, name = "menu_item_uk")})
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"restaurant", "dishRef"})
public class MenuItem extends BaseEntity {

    @Column(name = "actual_date", nullable = false)
    @NotNull
    private LocalDate actualDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;

    @Column(name = "restaurant_id", insertable = false, updatable = false)
    private int restaurantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dish_ref_id")
    @JsonIgnore
    // No cascade. Disable dishRef, already used in menu
    private DishRef dishRef;

    @Column(name = "dish_ref_id", insertable = false, updatable = false)
    private int dishRefId;

    public MenuItem(Integer id, LocalDate actualDate, Restaurant restaurant, DishRef dishRef) {
        super(id);
        this.actualDate = actualDate;
        this.restaurant = restaurant;
        this.dishRef = dishRef;
    }
}
