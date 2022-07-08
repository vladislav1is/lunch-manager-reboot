package com.redfox.restaurantvoting.web.restaurant.menu;

import com.redfox.restaurantvoting.mapper.MenuItemMapper;
import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.repository.DishRefRepository;
import com.redfox.restaurantvoting.repository.MenuItemRepository;
import com.redfox.restaurantvoting.repository.RestaurantRepository;
import com.redfox.restaurantvoting.service.MenuItemService;
import com.redfox.restaurantvoting.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.redfox.restaurantvoting.util.DateTimeUtil.atDayOrMin;
import static com.redfox.restaurantvoting.util.DateTimeUtil.atNextDayOrMax;
import static com.redfox.restaurantvoting.util.validation.Validations.assureIdConsistent;
import static com.redfox.restaurantvoting.util.validation.Validations.checkNew;

@Slf4j
public abstract class AbstractMenuItemController {

    @Autowired
    protected MenuItemMapper menuItemMapper;
    @Autowired
    protected MenuItemRepository menuItemRepository;
    @Autowired
    protected MenuItemService menuItemService;

    @Autowired
    protected DishRefRepository dishRefRepository;

    @Autowired
    protected RestaurantRepository restaurantRepository;

    @Transactional
    public Optional<MenuItem> find(int restaurantId, int id) {
        log.info("find {} for restaurantId={}", id, restaurantId);
        return menuItemRepository.findByRestaurantIdAndMenuItem(restaurantId, id);
    }

    public Optional<MenuItem> findWithDish(int restaurantId, int id) {
        log.info("findWithDish {} for restaurantId={}", id, restaurantId);
        return menuItemRepository.findWithDishByRestaurantIdAndMenuItem(restaurantId, id);
    }

    public List<MenuItem> getAllByRestaurant(int restaurantId) {
        log.info("getByRestaurant for restaurantId={}", restaurantId);
        return menuItemRepository.getAllByRestaurantId(restaurantId);
    }

    public List<MenuItem> getAllEnabledWithDishForToday(int restaurantId) {
        log.info("getAllEnabledWithDishForToday for restaurantId={}", restaurantId);
        return menuItemRepository.getAllEnabledWithDishByRestaurantIdAndDate(restaurantId, LocalDate.now());
    }

    public List<MenuItem> getAllWithDish(int restaurantId) {
        log.info("getAllWithDish for restaurantId={}", restaurantId);
        return menuItemRepository.getAllWithDishesByRestaurantId(restaurantId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId")
    })
    public void delete(int restaurantId, int id) {
        log.info("delete {} for restaurantId={}", id, restaurantId);
        MenuItem menuItem = menuItemRepository.checkBelong(restaurantId, id);
        menuItemRepository.delete(menuItem);
    }

    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId")
    })
    public void deleteWithDish(int restaurantId, int id) {
        log.info("deleteWithDish {} for restaurantId={}", id, restaurantId);
        menuItemService.delete(restaurantId, id);
    }

    public MenuItem create(int restaurantId, MenuItem menuItem, boolean named) {
        log.info("create {} for restaurantId={}", menuItem, restaurantId);
        checkNew(menuItem);
        return menuItemService.save(restaurantId, menuItem, named);
    }

    public void update(int restaurantId, MenuItem menuItem, int id, boolean named) {
        log.info("update {} for restaurantId={}, id={}", menuItem, restaurantId, id);
        assureIdConsistent(menuItem, id);
        menuItemService.save(restaurantId, menuItem, named);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "allRestaurantsWithMenu", allEntries = true),
            @CacheEvict(value = "restaurantWithMenu", key = "#restaurantId")
    })
    public void enable(int restaurantId, int id, boolean enabled) {
        log.info((enabled ? "enable {}" : "disable {}") + " for restaurantId={}", id, restaurantId);
        MenuItem menuItem = menuItemRepository.checkBelongWithDish(restaurantId, id);
        menuItem.getDishRef().setEnabled(enabled);
    }

    public List<MenuItem> findAllByDate(int restaurantId, LocalDate date) {
        log.info("findAllByDate for restaurantId={} and date={}", restaurantId, date);
        return menuItemRepository.getAllByRestaurantIdAndDate(restaurantId, date);
    }

    public List<MenuItem> findAllByDates(int restaurantId, LocalDate startDate, LocalDate endDate) {
        log.info("findAllByDates for restaurantId={} and dates=({}-{})", restaurantId, startDate, endDate);
        return menuItemRepository.getAllByRestaurantIdAndDates(restaurantId, startDate, endDate);
    }

    public List<MenuItem> findAllWithDishByDates(int restaurantId, @Nullable String startDate, @Nullable String endDate) {
        LocalDate ldStartDate = atDayOrMin(StringUtils.hasLength(startDate) ? LocalDate.parse(startDate, DateTimeUtil.DATE_FORMATTER) : null);
        LocalDate ldEndDate = atNextDayOrMax(StringUtils.hasLength(endDate) ? LocalDate.parse(endDate, DateTimeUtil.DATE_FORMATTER) : null);
        log.info("findAllWithDishByDates for restaurantId={} and dates=({}-{})", restaurantId, ldStartDate, ldEndDate);
        return menuItemRepository.getAllWithDishByDatesAndRestaurantId(restaurantId, ldStartDate, ldEndDate);
    }
}
