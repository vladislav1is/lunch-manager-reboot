package com.redfox.restaurantvoting.to;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.HasMenuItemToConstraint;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.mapper.Default;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuItemTo extends NamedTo implements HasMenuItemToConstraint {

    @NotBlank
    @JsonView(View.MenuItemWithoutEnabled.class)
    private String actualDate;

    @Range(min = 1, max = 1000_000)
    @JsonView(View.MenuItemWithoutEnabled.class)
    private int price;

    private boolean enabled = true;

    public MenuItemTo(Integer id, String name, String actualDate, int price, boolean enabled) {
        super(id, name);
        this.actualDate = actualDate;
        this.price = price;
        this.enabled = enabled;
    }

    @Default
    public MenuItemTo(Integer id, String name, String actualDate, int price) {
        this(id, name, actualDate, price, true);
    }
}