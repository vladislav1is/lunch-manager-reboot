package com.redfox.restaurantvoting.service;

import com.redfox.restaurantvoting.model.DishRef;
import com.redfox.restaurantvoting.repository.DishRefRepository;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DishRefService {
    private final RestaurantRepository restaurantRepository;
    private final DishRefRepository dishRefRepository;

    @Transactional
    public DishRef save(int restaurantId, DishRef dishRef) {
        dishRef.setRestaurant(restaurantRepository.getById(restaurantId));
        return dishRefRepository.save(dishRef);
    }
}