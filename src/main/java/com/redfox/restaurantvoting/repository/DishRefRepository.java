package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.error.restaurant.DishRefConstraintViolationException;
import com.redfox.restaurantvoting.error.restaurant.DishRefUsageException;
import com.redfox.restaurantvoting.model.DishRef;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface DishRefRepository extends BaseRepository<DishRef> {

    @Query("SELECT d FROM DishRef d WHERE d.id=:id AND d.restaurant.id=:restaurantId")
    Optional<DishRef> findByRestaurantIdAndDishRef(int restaurantId, int id);

    @Query("SELECT d FROM DishRef d WHERE d.name=:name AND d.restaurant.id=:restaurantId")
    Optional<DishRef> findByNameAndRestaurantId(String name, int restaurantId);

    @Query("SELECT d FROM DishRef d WHERE d.restaurant.id=:restaurantId ORDER BY d.name ASC")
    List<DishRef> getAllByRestaurantId(int restaurantId);

    default DishRef checkBelong(int restaurantId, int id) {
        return findByRestaurantIdAndDishRef(restaurantId, id).orElseThrow(
                () -> new DishRefConstraintViolationException("DishRef id=" + id + " doesn't belong to restaurant id=" + restaurantId));
    }

    default void checkUsage(int restaurantId) {
        if (!getAllByRestaurantId(restaurantId).isEmpty()) {
            throw new DishRefUsageException("Restaurant with id=" + restaurantId + " has dishes. Delete restaurant dishes.");
        }
    }
}