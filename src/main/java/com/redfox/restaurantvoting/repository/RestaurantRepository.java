package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.error.DataConflictException;
import com.redfox.restaurantvoting.model.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    //  https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#hql-distinct
    //  https://stackoverflow.com/questions/1346181/how-does-distinct-work-when-using-jpa-and-hibernate#53406102
    @QueryHints({
            @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false")
    })
    @Query("""
            SELECT DISTINCT r from Restaurant r
            JOIN FETCH r.menuItems mi
            JOIN FETCH mi.dishRef d
            WHERE r.enabled=true AND mi.actualDate=:date AND d.enabled=true
            ORDER BY r.name, d.name ASC
            """)
    List<Restaurant> getWithMenuByDate(LocalDate date);

    @Query("""
            SELECT r from Restaurant r
            JOIN FETCH r.menuItems mi
            JOIN FETCH mi.dishRef d
            WHERE r.id=:id AND r.enabled=true AND mi.actualDate=:date AND d.enabled=true
            ORDER BY d.name ASC
            """)
    Restaurant getWithMenuByRestaurantAndDate(int id, LocalDate date);

    @Query("SELECT r from Restaurant r WHERE r.id IN (:ids) ORDER BY r.name ASC")
    List<Restaurant> getIn(Collection<Integer> ids);

    @Query("SELECT r from Restaurant r WHERE r.enabled=true ORDER BY r.name ASC")
    List<Restaurant> getAllEnabled();

    default void checkAvailable(int id) {
        if (!getById(id).isEnabled()) {
            throw new DataConflictException("Restaurant " + id + " is unavailable");
        }
    }
}