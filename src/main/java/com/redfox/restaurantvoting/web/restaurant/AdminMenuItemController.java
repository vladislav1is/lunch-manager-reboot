package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import com.redfox.restaurantvoting.service.MenuItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.redfox.restaurantvoting.util.DateTimeUtil.*;
import static com.redfox.restaurantvoting.util.validation.Validations.assureIdConsistent;
import static com.redfox.restaurantvoting.util.validation.Validations.checkNew;

@RestController
@RequestMapping(value = AdminMenuItemController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class AdminMenuItemController {
    static final String REST_URL = "/api/admin/restaurants/{restaurantId}/menu-items";

    private final MenuItemRepository repository;
    private final MenuItemService service;

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> get(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("get for restaurantId={}, id={}", restaurantId, id);
        return ResponseEntity.of(repository.getByRestaurantIdAndMenuItem(restaurantId, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId")
    })
    public void delete(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("delete {} for restaurantId={}", id, restaurantId);
        MenuItem menuItem = repository.checkBelong(restaurantId, id);
        repository.delete(menuItem);
    }

    @GetMapping
    public List<MenuItem> getByRestaurant(@PathVariable int restaurantId) {
        log.info("getByRestaurant for restaurantId={}", restaurantId);
        return repository.getByRestaurantId(restaurantId);
    }

    @GetMapping("/by-date")
    public List<MenuItem> getByRestaurantAndDate(@PathVariable int restaurantId,
                                                 @Nullable @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        date = atDayOrNow(date);
        log.info("getByRestaurantAndDate for restaurantId={} and date={}", restaurantId, date);
        return repository.getByRestaurantIdAndDate(restaurantId, date);
    }

    @GetMapping("/filter")
    public List<MenuItem> getBetweenDatesByRestaurant(@PathVariable int restaurantId,
                                                      @Nullable @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                      @Nullable @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        startDate = atDayOrMin(startDate);
        endDate = atNextDayOrMax(endDate);
        log.info("getBetweenDatesByRestaurant for restaurantId={} and dates=({} - {})", restaurantId, startDate, endDate);
        return repository.getBetweenDatesByRestaurantId(restaurantId, startDate, endDate);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true, condition = "T(java.time.LocalDate).now().equals(#menuItem.actualDate)"),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId", condition = "T(java.time.LocalDate).now().equals(#menuItem.actualDate)")
    })
    public ResponseEntity<MenuItem> createWithLocation(@PathVariable int restaurantId, @Valid @RequestBody MenuItem menuItem) {
        log.info("create {} for restaurantId={}", menuItem, restaurantId);
        checkNew(menuItem);
        MenuItem created = service.save(restaurantId, menuItem);
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
        log.info("update {} for restaurantId={}, id={}", menuItem, restaurantId, id);
        assureIdConsistent(menuItem, id);
        repository.checkBelong(restaurantId, id);
        service.save(restaurantId, menuItem);
    }
}