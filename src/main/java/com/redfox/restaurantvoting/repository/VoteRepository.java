package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.model.Vote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
public interface VoteRepository extends BaseRepository<Vote> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Vote v WHERE v.restaurantId=:restaurantId")
    void deleteByRestaurantId(int restaurantId);

    @Query("SELECT v FROM Vote v WHERE v.actualDate=:date AND v.user.id=:userId")
    Vote getByDateAndUserId(LocalDate date, int userId);

    @Query("SELECT v FROM Vote v WHERE v.user.id=:userId")
    List<Vote> getAllByUserId(int userId);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.actualDate=:date AND v.restaurantId=:restaurantId")
    int countByDateAndRestaurantId(LocalDate date, int restaurantId);
}