package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.error.DataConflictException;
import com.redfox.restaurantvoting.model.MenuItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface MenuItemRepository extends BaseRepository<MenuItem> {

    @Transactional
    @Modifying
    @Query("DELETE FROM MenuItem mi WHERE mi.restaurant.id=:restaurantId")
    void deleteByRestaurantId(int restaurantId);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id=:restaurantId ORDER BY mi.actualDate DESC, mi.dishRef.name ASC")
    List<MenuItem> getByRestaurantId(int restaurantId);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.id=:id AND mi.restaurant.id=:restaurantId")
    Optional<MenuItem> getByRestaurantIdAndMenuItem(int restaurantId, int id);

    @Query("SELECT mi from MenuItem mi WHERE mi.restaurant.id=:restaurantId AND mi.actualDate = :date ORDER BY mi.dishRef.name ASC")
    List<MenuItem> getByRestaurantIdAndDate(int restaurantId, LocalDate date);

    @Query("""
            SELECT mi from MenuItem mi
            WHERE mi.restaurant.id=:restaurantId AND mi.actualDate >= :startDate AND mi.actualDate < :endDate
            ORDER BY mi.actualDate DESC
            """)
    List<MenuItem> getBetweenDatesByRestaurantId(LocalDate startDate, LocalDate endDate, int restaurantId);

    default MenuItem checkBelong(int restaurantId, int id) {
        return getByRestaurantIdAndMenuItem(restaurantId, id).orElseThrow(
                () -> new DataConflictException("MenuItem id=" + id + " doesn't belong to Restaurant id=" + restaurantId));
    }
}