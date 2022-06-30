package com.redfox.restaurantvoting.web.restaurant.dish;

import com.redfox.restaurantvoting.model.DishRef;
import com.redfox.restaurantvoting.repository.DishRefRepository;
import com.redfox.restaurantvoting.service.DishRefService;
import com.redfox.restaurantvoting.web.restaurant.AdminRestaurantController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.redfox.restaurantvoting.util.validation.Validations.assureIdConsistent;
import static com.redfox.restaurantvoting.util.validation.Validations.checkNew;

@RestController
@RequestMapping(value = AdminDishRefController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
public class AdminDishRefController {
    static final String REST_URL =  AdminRestaurantController.REST_URL + "/{restaurantId}/dish-ref";

    private final DishRefRepository repository;
    private final DishRefService service;

    @GetMapping("/{id}")
    public ResponseEntity<DishRef> get(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("get for restaurantId={}, id={}", restaurantId, id);
        return ResponseEntity.of(repository.findByRestaurantIdAndDishRef(restaurantId, id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    // No cache evict: couldn't delete, if used in MenuItem
    public void delete(@PathVariable int restaurantId, @PathVariable int id) {
        log.info("delete for restaurantId={}, id={}", restaurantId, id);
        DishRef dishRef = repository.checkBelong(restaurantId, id);
        repository.delete(dishRef);
    }

    @GetMapping
    public List<DishRef> getByRestaurant(@PathVariable int restaurantId) {
        log.info("getByRestaurant for restaurantId={}", restaurantId);
        return repository.getAllByRestaurantId(restaurantId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DishRef> createWithLocation(@PathVariable int restaurantId, @Valid @RequestBody DishRef dishRef) {
        log.info("create {} for restaurantId={}", dishRef, restaurantId);
        checkNew(dishRef);
        DishRef created = service.save(restaurantId, dishRef);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(restaurantId, created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // https://stackoverflow.com/questions/25379051/548473
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId")
    })
    public void update(@PathVariable int restaurantId, @PathVariable int id, @Valid @RequestBody DishRef dishRef) {
        log.info("update {} for restaurantId={}, id={}", dishRef, restaurantId, id);
        assureIdConsistent(dishRef, id);
        repository.checkBelong(restaurantId, id);
        service.save(restaurantId, dishRef);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId")
    })
    public void enable(@PathVariable int restaurantId, @PathVariable int id, @RequestParam boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        DishRef dishRef = repository.checkBelong(restaurantId, id);
        dishRef.setEnabled(enabled);
    }
}