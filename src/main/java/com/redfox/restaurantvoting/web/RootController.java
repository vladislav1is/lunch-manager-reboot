package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.UserRepository;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Hidden
@Controller
@AllArgsConstructor
public class RootController {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    @GetMapping("/")
    public String root() {
        return "redirect:restaurants";
    }

    @GetMapping("/restaurants")
    public String getRestaurants(ModelMap model) {
        model.addAttribute("restaurants", restaurantRepository.getAllEnabled());
        return "restaurants";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "users";
    }
}