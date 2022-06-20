package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.error.DishRefConstraintViolationException;
import com.redfox.restaurantvoting.error.DataDisabledException;
import com.redfox.restaurantvoting.model.DishRef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.redfox.restaurantvoting.util.validation.Validations.checkRestaurantUsage;

@Transactional(readOnly = true)
public interface DishRefRepository extends BaseRepository<DishRef> {

    @Query("SELECT d FROM DishRef d WHERE d.id=:id AND d.restaurant.id=:restaurantId")
    Optional<DishRef> findByRestaurantIdAndDishRef(int restaurantId, int id);

    @Query("SELECT d FROM DishRef d WHERE d.restaurant.id=:restaurantId ORDER BY d.name ASC")
    List<DishRef> getAllByRestaurantId(int restaurantId);

    default DishRef checkBelong(int restaurantId, int id) {
        return findByRestaurantIdAndDishRef(restaurantId, id).orElseThrow(
                () -> new DishRefConstraintViolationException("DishRef id=" + id + " doesn't belong to restaurant id=" + restaurantId));
    }

    default void checkAvailable(int id) {
        if (!getById(id).isEnabled()) {
            throw new DataDisabledException("DishRef " + id + " is unavailable");
        }
    }

    default void checkUsage(int restaurantId) {
        checkRestaurantUsage(getAllByRestaurantId(restaurantId).isEmpty(), restaurantId);
    }
}