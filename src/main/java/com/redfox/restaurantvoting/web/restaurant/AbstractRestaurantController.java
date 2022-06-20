package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.mapper.RestaurantMapper;
import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.DishRefRepository;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.repository.UserRepository;
import com.redfox.restaurantvoting.to.RestaurantWithMenu;
import com.redfox.restaurantvoting.util.Restaurants;
import com.redfox.restaurantvoting.util.validation.AdminRestaurantsUtil;
import com.redfox.restaurantvoting.web.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.redfox.restaurantvoting.util.validation.Validations.assureIdConsistent;
import static com.redfox.restaurantvoting.util.validation.Validations.checkNew;

@Slf4j
public abstract class AbstractRestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    protected RestaurantMapper restaurantMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DishRefRepository dishRefRepository;

    @Autowired
    private UniqueRestaurantValidator nameValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(nameValidator);
    }

    @Transactional
    public Optional<Restaurant> findByRestaurant(int id) {
        log.info("findById {}", id);
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        restaurant.ifPresent(r -> restaurantRepository.checkAvailable(r.id()));
        return restaurant;
    }

    @Transactional
    public Optional<Restaurant> findWithMenuByRestaurantForToday(int id) {
        log.info("getWithMenuByRestaurantForToday {}", id);
        restaurantRepository.checkAvailable(id);
        return restaurantRepository.findWithMenuByRestaurantAndDate(id, LocalDate.now());
    }

    @Cacheable("restaurants")
    public List<Restaurant> getAll() {
        log.info("getAll");
        User user = SecurityUtil.authUser();
        if (user.hasRole(Role.R_ADMIN)) {
            return restaurantRepository.getIn(user.getAdminRestaurants());
        } else {
            return restaurantRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        }
    }

    @Cacheable("allEnabledRestaurants")
    public List<Restaurant> getAllEnabled() {
        log.info("getAllEnabled");
        return restaurantRepository.getAllEnabled();
    }

    @Cacheable("allRestaurantsWithMenu")
    public List<RestaurantWithMenu> getWithMenuForToday() {
        log.info("getWithMenuForToday");
        List<Restaurant> restaurants = restaurantRepository.getWithMenuByDate(LocalDate.now());
        return restaurantMapper.toToList(restaurants);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurants", allEntries = true),
            @CacheEvict(value = "allEnabledRestaurants", allEntries = true),
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#id")
    })
    public void delete(int id) {
        log.info("delete {}", id);
        dishRefRepository.checkUsage(id);
        restaurantRepository.deleteExisted(id);
    }

    @Transactional
    public Restaurant create(Restaurant restaurant) {
        log.info("create {}", restaurant);
        checkNew(restaurant);
        Restaurant created = prepareAndSave(restaurant);
        User user = SecurityUtil.authUser();
        if (user.hasRole(Role.R_ADMIN)) {
            AdminRestaurantsUtil.addRestaurant(user, created.getId());
            userRepository.save(user);
        }
        return created;
    }

    protected Restaurant prepareAndSave(Restaurant restaurant) {
        return restaurantRepository.save(Restaurants.prepareToSave(restaurant));
    }

    public void update(Restaurant restaurant, int id) {
        log.info("update {} with id={}", restaurant, id);
        assureIdConsistent(restaurant, id);
        prepareAndSave(restaurant);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "restaurants", allEntries = true),
            @CacheEvict(value = "allEnabledRestaurants", allEntries = true),
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#id")
    })
    public void enable(int id, boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        Restaurant restaurant = restaurantRepository.getById(id);
        restaurant.setEnabled(enabled);
    }
}
