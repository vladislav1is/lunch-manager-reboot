package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Hidden
@Controller
@AllArgsConstructor
public class RootController {

    private final RestaurantRepository restaurantRepository;

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

    @Secured({"ROLE_ADMIN", "ROLE_R_ADMIN"})
    @GetMapping("/restaurants/editor")
    public String editRestaurants() {
        return "restaurants-editor";
    }

    @Transactional
    @GetMapping("/restaurants/{restaurantId}/menu-items")
    public String getDishesToday(@PathVariable int restaurantId, Model model) {
        setRestaurantAttributes(restaurantId, model);
        restaurantRepository.checkAvailable(restaurantId);
        return "menu-items";
    }

    protected void setRestaurantAttributes(int restaurantId, Model model) {
        Restaurant restaurant = restaurantRepository.getExisted(restaurantId);
        model.addAttribute("restaurantName", restaurant.getName());
        model.addAttribute("restaurantId", restaurant.getId());
    }

    @Secured({"ROLE_ADMIN", "ROLE_R_ADMIN"})
    @GetMapping("/restaurants/{restaurantId}/menu-items/editor")
    public String editDishes(@PathVariable int restaurantId, Model model, @AuthenticationPrincipal AuthUser authUser) {
        setRestaurantAttributes(restaurantId, model);
        User user = authUser.getUser();
        if (user.hasRole(Role.R_ADMIN) && !user.getAdminRestaurants().contains(restaurantId)) {
            throw new AccessDeniedException("You have no rights for restaurant " + restaurantId);
        }
        return "menu-items-editor";
    }

    @Transactional
    @GetMapping("/restaurants/{restaurantId}/vote")
    public String voteForRestaurant(@PathVariable int restaurantId, Model model) {
        setRestaurantAttributes(restaurantId, model);
        restaurantRepository.checkAvailable(restaurantId);
        return "restaurant-vote";
    }
}