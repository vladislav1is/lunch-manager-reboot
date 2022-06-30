package com.redfox.restaurantvoting.web;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.model.Vote;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.VoteRepository;
import com.redfox.restaurantvoting.service.VoteService;
import com.redfox.restaurantvoting.to.VoteTo;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Hidden
@Controller
@AllArgsConstructor
public class RootController {

    private final RestaurantRepository restaurantRepository;

    private final VoteRepository voteRepository;
    public final VoteService voteService;

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
    public String getRestaurants(Model model, @AuthenticationPrincipal AuthUser authUser) {
        Optional<Vote> vote = voteRepository.findByDateAndUserId(LocalDate.now(), authUser.id());
        VoteTo userVote = vote.map(value -> new VoteTo(value.id(), value.getRestaurantId())).orElse(null);
        model.addAttribute("userVote", userVote);
        return "restaurants";
    }

    @Secured({"ROLE_ADMIN", "ROLE_R_ADMIN"})
    @GetMapping("/restaurants/editor")
    public String editRestaurants() {
        return "restaurants-editor";
    }

    @GetMapping("/restaurants/{restaurantId}/menu-items")
    public String getDishesToday(@PathVariable int restaurantId, Model model) {
        setRestaurantAttributes(restaurantId, model);
        return "menu-items";
    }

    @Transactional
    protected Restaurant setRestaurantAttributes(int restaurantId, Model model) {
        Restaurant restaurant = restaurantRepository.getExisted(restaurantId);
        restaurantRepository.checkAvailable(restaurantId);
        model.addAttribute("restaurantName", restaurant.getName());
        model.addAttribute("restaurantId", restaurant.getId());
        return restaurant;
    }

    @Transactional
    @Secured({"ROLE_ADMIN", "ROLE_R_ADMIN"})
    @GetMapping("/restaurants/{restaurantId}/menu-items/editor")
    public String editDishes(@PathVariable int restaurantId, Model model, @AuthenticationPrincipal AuthUser authUser) {
        Restaurant restaurant = setRestaurantAttributes(restaurantId, model);
        User user = authUser.getUser();
        if (user.hasRole(Role.R_ADMIN)) {
            List<Restaurant> adminRestaurants = restaurantRepository.getIn(user.getAdminRestaurants());
            if (!adminRestaurants.contains(restaurant)) {
                throw new AccessDeniedException("You have no rights for restaurant " + restaurantId);
            }
        }
        return "menu-items-editor";
    }

    @Transactional
    @GetMapping("/restaurants/{restaurantId}/vote")
    public String voteForRestaurant(@PathVariable int restaurantId, Model model) {
        setRestaurantAttributes(restaurantId, model);
        int votesCount = voteRepository.countByDateAndRestaurantId(LocalDate.now(), restaurantId);
        model.addAttribute("votesCount", votesCount);
        return "restaurant-vote";
    }
}