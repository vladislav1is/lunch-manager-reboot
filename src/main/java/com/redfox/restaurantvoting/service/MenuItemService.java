package com.redfox.restaurantvoting.service;

import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.repository.DishRefRepository;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class MenuItemService {
    private final RestaurantRepository restaurantRepository;
    private final DishRefRepository dishRefRepository;
    private final MenuItemRepository menuItemRepository;

    @Transactional
    public MenuItem save(int restaurantId, MenuItem menuItem) {
        dishRefRepository.checkBelong(restaurantId, menuItem.getDishRefId());
        menuItem.setRestaurant(restaurantRepository.getById(restaurantId));
        return menuItemRepository.save(menuItem);
    }
}
