package com.redfox.restaurantvoting.to;

import com.redfox.restaurantvoting.HasRestaurantConstraint;
import com.redfox.restaurantvoting.util.validation.NoHtml;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantWithVisitors extends NamedTo implements HasRestaurantConstraint {

    public RestaurantWithVisitors(Integer id, String name, String address, int visitors) {
        super(id, name);
        this.address = address;
        this.visitors = visitors;
    }

    @Size(max = 1024)
    @NoHtml
    @NotBlank
    String address;

    int visitors;
}