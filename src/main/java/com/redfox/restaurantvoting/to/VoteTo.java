package com.redfox.restaurantvoting.to;

import lombok.*;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VoteTo extends BaseTo {

    public VoteTo(Integer id, int restaurantId) {
        super(id);
        this.restaurantId = restaurantId;
    }

    int restaurantId;
}
