package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.error.restaurant.VoteUsageException;
import com.redfox.restaurantvoting.model.Vote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.restaurantId=:restaurantId")
    void deleteByRestaurantId(int restaurantId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.restaurantId=:restaurantId AND v.actualDate=:date")
    void deleteByRestaurantIdAndDate(int restaurantId, LocalDate date);

    @Query("SELECT v FROM Vote v WHERE v.actualDate=:date AND v.user.id=:userId")
    Optional<Vote> findByDateAndUserId(LocalDate date, int userId);

    @Query("SELECT v FROM Vote v WHERE v.user.id=:userId ORDER BY v.actualDate DESC")
    List<Vote> getAllByUserId(int userId);

    @Query("SELECT v FROM Vote v WHERE v.restaurantId=:restaurantId ORDER BY v.actualDate DESC")
    List<Vote> getAllByRestaurantId(int restaurantId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.actualDate=:date AND v.restaurantId=:restaurantId")
    int countByDateAndRestaurantId(LocalDate date, int restaurantId);

    default void checkUsage(int restaurantId) {
        if (!getAllByRestaurantId(restaurantId).isEmpty()) {
            throw new VoteUsageException("Restaurant with id=" + restaurantId + " has votes. Delete restaurant votes.");
        }
    }
}