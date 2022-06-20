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

    @Query("SELECT mi FROM MenuItem mi WHERE mi.id=:id AND mi.restaurant.id=:restaurantId")
    Optional<MenuItem> findByRestaurantIdAndMenuItem(int restaurantId, int id);

    @Query("""
            SELECT mi from MenuItem mi
            WHERE mi.restaurant.id=:restaurantId AND mi.actualDate=:date AND mi.dishRef.id=:dishRefId
            """)
    Optional<MenuItem> findByDateAndDishRefId(int restaurantId, LocalDate date, int dishRefId);

    @Query("""
            SELECT mi from MenuItem mi
            WHERE mi.restaurant.id=:restaurantId AND mi.actualDate=:date AND mi.dishRef.name=:name
            """)
    Optional<MenuItem> findByNameDateAndRestaurantId(int restaurantId, LocalDate date, String name);

    @Query("""
            SELECT mi from MenuItem mi
            JOIN FETCH mi.dishRef d
            WHERE mi.id=:id AND mi.restaurant.id=:restaurantId
            """)
    Optional<MenuItem> findWithDishByRestaurantIdAndMenuItem(int restaurantId, int id);

    @Transactional
    @Modifying
    @Query("DELETE FROM MenuItem mi WHERE mi.restaurant.id=:restaurantId")
    void deleteByRestaurantId(int restaurantId);

    @Query("SELECT mi FROM MenuItem mi WHERE mi.restaurant.id=:restaurantId ORDER BY mi.actualDate DESC, mi.dishRef.name ASC")
    List<MenuItem> getAllByRestaurantId(int restaurantId);

    @Query("""
            SELECT mi FROM MenuItem mi
            JOIN FETCH mi.dishRef d
            WHERE mi.restaurant.id=:restaurantId
            ORDER BY mi.actualDate DESC, mi.dishRef.name ASC
            """)
    List<MenuItem> getAllWithDishesByRestaurantId(int restaurantId);

    @Query("SELECT mi from MenuItem mi WHERE mi.restaurant.id=:restaurantId AND mi.actualDate = :date ORDER BY mi.dishRef.name ASC")
    List<MenuItem> getAllByRestaurantIdAndDate(int restaurantId, LocalDate date);

    @Query("""
            SELECT mi from MenuItem mi
            WHERE mi.restaurant.id=:restaurantId AND mi.actualDate >= :startDate AND mi.actualDate < :endDate
            ORDER BY mi.actualDate DESC
            """)
    List<MenuItem> getAllByRestaurantIdAndDates(int restaurantId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT mi from MenuItem mi
            JOIN FETCH mi.dishRef d
            WHERE mi.restaurant.id=:restaurantId AND mi.actualDate >= :startDate AND mi.actualDate < :endDate
            ORDER BY d.name ASC
            """)
    List<MenuItem> getAllWithDishByDatesAndRestaurantId(int restaurantId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT mi from MenuItem mi
            JOIN FETCH mi.dishRef d
            WHERE mi.restaurant.id=:restaurantId AND mi.restaurant.enabled=true AND d.enabled=true AND mi.actualDate=:date
            ORDER BY d.name ASC
            """)
    List<MenuItem> getAllEnabledWithDishByRestaurantIdAndDate(int restaurantId, LocalDate date);

    @Query("""
            SELECT mi from MenuItem mi
            JOIN FETCH mi.dishRef d
            WHERE mi.restaurant.id=:restaurantId AND d.name=:dishName
            ORDER BY d.name ASC
            """)
    List<MenuItem> getByDishNameAndRestaurantId(int restaurantId, String dishName);

    default MenuItem checkBelong(int restaurantId, int id) {
        return findByRestaurantIdAndMenuItem(restaurantId, id).orElseThrow(
                () -> new DataConflictException("MenuItem id=" + id + " doesn't belong to Restaurant id=" + restaurantId));
    }

    default MenuItem checkBelongWithDish(int restaurantId, int id) {
        return findWithDishByRestaurantIdAndMenuItem(restaurantId, id).orElseThrow(
                () -> new DataConflictException("MenuItem id=" + id + " doesn't belong to Restaurant id=" + restaurantId));
    }
}