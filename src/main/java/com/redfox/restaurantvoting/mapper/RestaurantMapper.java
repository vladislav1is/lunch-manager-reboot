package com.redfox.restaurantvoting.mapper;

import com.redfox.restaurantvoting.model.DishRef;
import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.to.RestaurantWithMenu;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantMapper extends BaseMapper<Restaurant, RestaurantWithMenu> {

    @Mapping(target = "dishRefs", expression = "java(getDishRefs(restaurant))")
    @Override
    RestaurantWithMenu toTo(Restaurant restaurant);

    default List<DishRef> getDishRefs(Restaurant restaurant) {
        return restaurant.getMenuItems().stream()
                .map(MenuItem::getDishRef).toList();
    }
}
