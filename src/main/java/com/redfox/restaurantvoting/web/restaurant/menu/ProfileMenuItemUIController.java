package com.redfox.restaurantvoting.web.restaurant.menu;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.to.MenuItemTo;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@RestController
@RequestMapping(value = ProfileMenuItemUIController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProfileMenuItemUIController extends AbstractMenuItemController {
    static final String REST_URL = "/profile/restaurants/{restaurantId}/menu-items";

    @GetMapping
    @JsonView(View.MenuItemWithoutEnabled.class)
    public List<MenuItemTo> getAllEnabledForToday(@PathVariable int restaurantId) {
        return menuItemMapper.toToList(super.getAllEnabledWithDishForToday(restaurantId));
    }
}