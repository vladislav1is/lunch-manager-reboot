package com.redfox.restaurantvoting.web.restaurant.menu;

import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.util.DateTimeUtil;
import com.redfox.restaurantvoting.util.validation.AdminRestaurantsUtil;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.redfox.restaurantvoting.util.DateTimeUtil.*;

@RestController
@RequestMapping(value = AdminMenuItemController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AdminMenuItemController extends AbstractMenuItemController {
    static final String REST_URL = AdminRestaurantsUtil.REST_URL + "/{restaurantId}/menu-items";

    private final UniqueMenuItemValidator menuValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(menuValidator);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> get(@PathVariable int restaurantId, @PathVariable int id) {
        return ResponseEntity.of(super.find(restaurantId, id));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int restaurantId, @PathVariable int id) {
        super.delete(restaurantId, id);
    }

    @Override
    @GetMapping
    public List<MenuItem> getAllByRestaurant(@PathVariable int restaurantId) {
        return super.getAllByRestaurant(restaurantId);
    }

    @Override
    @GetMapping("/by-date")
    public List<MenuItem> findAllByDate(@PathVariable int restaurantId,
                                        @Nullable @RequestParam @DateTimeFormat(pattern = DateTimeUtil.DATE_PATTERN) LocalDate date) {
        return super.findAllByDate(restaurantId, atDayOrNow(date));
    }

    @GetMapping("/filter")
    public List<MenuItem> filterByDates(@PathVariable int restaurantId,
                                         @Nullable @RequestParam @DateTimeFormat(pattern = DateTimeUtil.DATE_PATTERN) LocalDate startDate,
                                         @Nullable @RequestParam @DateTimeFormat(pattern = DateTimeUtil.DATE_PATTERN) LocalDate endDate) {
        return super.findAllByDates(restaurantId,  atDayOrMin(startDate), atNextDayOrMax(endDate));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true, condition = "T(java.time.LocalDate).now().equals(#menuItem.actualDate)"),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId", condition = "T(java.time.LocalDate).now().equals(#menuItem.actualDate)")
    })
    public ResponseEntity<MenuItem> createWithLocation(@PathVariable int restaurantId, @Valid @RequestBody MenuItem menuItem) {
        MenuItem created = super.create(restaurantId, menuItem, false);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true, condition = "T(java.time.LocalDate).now().equals(#menuItem.actualDate)"),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId", condition = "T(java.time.LocalDate).now().equals(#menuItem.actualDate)")
    })
    public void update(@PathVariable int restaurantId, @Valid @RequestBody MenuItem menuItem, @PathVariable int id) {
        super.update(restaurantId, menuItem, id, false);
    }
}