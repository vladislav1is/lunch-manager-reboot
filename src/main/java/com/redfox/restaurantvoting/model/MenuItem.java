package com.redfox.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.HasMenuItemConstraint;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.util.DateTimeUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "menu_item", uniqueConstraints = {@UniqueConstraint(columnNames = {"actual_date", "dish_ref_id"}, name = "menu_item_uk")})
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true, exclude = {"restaurant", "dishRef"})
public class MenuItem extends BaseEntity implements HasMenuItemConstraint {
    @Column(name = "actual_date", nullable = false)
    @NotNull
    @DateTimeFormat(pattern = DateTimeUtil.DATE_PATTERN)
    @JsonView(View.MenuItemWithoutRestaurantId.class)
    private LocalDate actualDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Restaurant restaurant;

    @Column(name = "restaurant_id", insertable = false, updatable = false)
    private Integer restaurantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dish_ref_id")
    @JsonIgnore
    // No cascade. Disable dishRef, already used in menu
    private DishRef dishRef;

    @Column(name = "dish_ref_id", insertable = false, updatable = false)
    @JsonView(View.MenuItemWithoutRestaurantId.class)
    private Integer dishRefId;

    public MenuItem(Integer id, @NotNull LocalDate actualDate, Restaurant restaurant, DishRef dishRef) {
        super(id);
        this.actualDate = actualDate;
        this.restaurant = restaurant;
        this.restaurantId = restaurant.id();
        this.dishRef = dishRef;
        this.dishRefId = dishRef.id();
    }
}
