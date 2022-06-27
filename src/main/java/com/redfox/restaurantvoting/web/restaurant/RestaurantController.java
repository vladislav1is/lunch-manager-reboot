package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.to.RestaurantWithMenu;
import com.redfox.restaurantvoting.to.RestaurantWithVisitors;
import com.redfox.restaurantvoting.util.DateTimeUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    @GetMapping("/menu-today")
    public List<RestaurantWithMenu> getWithMenuForToday() {
        return super.getWithMenuForToday();
    }


    @Transactional
    @GetMapping("/{id}/menu-today")
    @Cacheable("restaurantWithMenu")
    public ResponseEntity<RestaurantWithMenu> getWithMenuByRestaurantForToday(@PathVariable int id) {
        restaurantRepository.checkAvailable(id);
        Optional<Restaurant> restaurant = super.findWithMenuByRestaurantForToday(id);
        if (restaurant.isEmpty()) {
            return ResponseEntity.of(Optional.empty());
        } else {
            return ResponseEntity.ok(restaurantMapper.toTo(restaurant.get()));
        }
    }

    @GetMapping("/{id}/visitors-today")
    public ResponseEntity<RestaurantWithVisitors> getWithVisitorsByRestaurantAndDate(@PathVariable int id, @RequestParam @DateTimeFormat(pattern = DateTimeUtil.DATE_PATTERN) @Nullable LocalDate date) {
        return ResponseEntity.ok(super.findWithVisitorsByRestaurantAndDate(id, date));
    }
}