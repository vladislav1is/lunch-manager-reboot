package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Hidden
@Controller
@AllArgsConstructor
public class RootController {

    private final RestaurantRepository repository;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:restaurants";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String getUsers() {
        return "users";
    }

    @GetMapping("/restaurants")
    public String getRestaurants() {
        return "restaurants";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/restaurants/editor")
    public String editRestaurants() {
        return "restaurants-editor";
    }

    @GetMapping("/restaurants/{restaurantId}/menu-items")
    public String getDishesToday(@PathVariable int restaurantId, Model model) {
        Restaurant restaurant = repository.findById(restaurantId).orElseThrow();
        model.addAttribute("restaurantName", restaurant.getName());
        model.addAttribute("restaurantId", restaurant.getId());
        return "menu-items";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/restaurants/{restaurantId}/menu-items/editor")
    public String editDishes(@PathVariable int restaurantId, Model model) {
        Restaurant restaurant = repository.findById(restaurantId).orElseThrow();
        model.addAttribute("restaurantName", restaurant.getName());
        model.addAttribute("restaurantId", restaurant.getId());
        return "menu-items-editor";
    }
}