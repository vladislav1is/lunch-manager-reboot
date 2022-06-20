package com.redfox.restaurantvoting.web.restaurant.menu;

import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.to.MenuItemTo;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Hidden
@RestController
@RequestMapping(value = AdminMenuItemUIController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AdminMenuItemUIController extends AbstractMenuItemController {
    static final String REST_URL = "/admin/restaurants/{restaurantId}/menu-items";

    private final UniqueMenuItemToValidator menuValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(menuValidator);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemTo> getWithDish(@PathVariable int restaurantId, @PathVariable int id) {
        Optional<MenuItem> menuItem = super.findWithDish(restaurantId, id);
        if (menuItem.isEmpty()) {
            return ResponseEntity.of(Optional.empty());
        } else {
            return ResponseEntity.ok(menuItemMapper.toTo(menuItem.get()));
        }
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWithDish(@PathVariable int restaurantId, @PathVariable int id) {
        super.deleteWithDish(restaurantId, id);
    }

    @GetMapping
    public List<MenuItemTo> getAll(@PathVariable int restaurantId) {
        return menuItemMapper.toToList(super.getAllWithDish(restaurantId));
    }

    @GetMapping("/filter")
    public List<MenuItemTo> filterByDates(@PathVariable int restaurantId, @RequestParam @Nullable String startDate, @RequestParam @Nullable String endDate) {
        return menuItemMapper.toToList(super.findAllWithDishByDates(restaurantId, startDate, endDate));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true, condition = "T(java.time.LocalDate).now().equals(#menuItemTo.actualDate)"),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId", condition = "T(java.time.LocalDate).now().equals(#menuItemTo.actualDate)")
    })
    public void createOrUpdate(@PathVariable int restaurantId, @Valid MenuItemTo menuItemTo) {
        MenuItem created = menuItemMapper.toEntity(menuItemTo);
        if (created.isNew()) {
            super.create(restaurantId, created, true);
        } else {
            super.update(restaurantId, created, menuItemTo.id(), true);
        }
    }

    @Override
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int restaurantId, @PathVariable int id, @RequestParam boolean enabled) {
        super.enable(restaurantId, id, enabled);
    }
}