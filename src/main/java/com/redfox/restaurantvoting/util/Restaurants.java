package com.redfox.restaurantvoting.util;

import com.redfox.restaurantvoting.model.Restaurant;
import lombok.experimental.UtilityClass;

import java.util.Objects;

@UtilityClass
public class Restaurants {

    public static Restaurant prepareToSave(Restaurant restaurant) {
        restaurant.setName(restaurant.getName().strip());
        String address = restaurant.getAddress();
        if (Objects.nonNull(address)) {
            restaurant.setAddress(address.strip());
        }
        return restaurant;
    }
}