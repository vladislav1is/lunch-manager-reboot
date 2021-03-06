package com.redfox.restaurantvoting.service;

import com.redfox.restaurantvoting.model.DishRef;
import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.repository.DishRefRepository;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuItemService {

    private final RestaurantRepository restaurantRepository;

    private final MenuItemRepository menuItemRepository;

    private final DishRefRepository dishRefRepository;

    @Transactional
    public MenuItem save(int restaurantId, MenuItem menuItem, boolean named) {
        Restaurant restaurant = restaurantRepository.getById(restaurantId);
        DishRef dishRef;
        if (named) {
            if (menuItem.getId() != null) {
                menuItemRepository.checkBelong(restaurantId, menuItem.id());
            }
            DishRef createdDish = menuItem.getDishRef();
            Optional<DishRef> dbDish = dishRefRepository.findByNameAndRestaurantId(createdDish.getName(), restaurantId);
            dbDish.ifPresent(ref -> createdDish.setId(ref.id()));
            dishRef = dishRefRepository.save(new DishRef(createdDish.getId(), createdDish.getName().strip(), createdDish.getPrice(), restaurant));
        } else {
            int dishRefId = menuItem.getDishRefId();
            dishRefRepository.getExisted(dishRefId);
            dishRef = dishRefRepository.checkBelong(restaurantId, dishRefId);
        }
        menuItem.setRestaurant(restaurant);
        menuItem.setRestaurantId(restaurantId);
        menuItem.setDishRef(dishRef);
        menuItem.setDishRefId(dishRef.id());
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public void delete(int restaurantId, int id) {
        MenuItem menuItem = menuItemRepository.checkBelongWithDish(restaurantId, id);
        DishRef dishRef = menuItem.getDishRef();
        menuItemRepository.delete(id);
        List<MenuItem> menuItems = menuItemRepository.getByDishNameAndRestaurantId(restaurantId, dishRef.getName());
        if (menuItems.isEmpty()) {
            dishRefRepository.delete(dishRef.id());
        }
    }
}
