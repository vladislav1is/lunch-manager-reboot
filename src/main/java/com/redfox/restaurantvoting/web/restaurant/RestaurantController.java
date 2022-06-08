package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.to.RestaurantWithMenu;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = RestaurantController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController extends AbstractRestaurantController {
    static final String REST_URL = "/api/restaurants";

    @Override
    @GetMapping
    public List<Restaurant> getAllEnabled() {
        return super.getAllEnabled();
    }

    @Override
    @GetMapping("/menu_today")
    public List<RestaurantWithMenu> getWithMenuForToday() {
        return super.getWithMenuForToday();
    }


    @Override
    @GetMapping("/{id}/menu_today")
    public RestaurantWithMenu getWithMenuByRestaurantForToday(@PathVariable int id) {
        return super.getWithMenuByRestaurantForToday(id);
    }
}