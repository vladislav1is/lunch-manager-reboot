package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.error.DataDisabledException;
import com.redfox.restaurantvoting.model.Restaurant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.QueryHint;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface RestaurantRepository extends BaseRepository<Restaurant> {

    @Query("""
            SELECT r from Restaurant r
            WHERE r.name=:name AND r.address=:address
            ORDER BY r.name ASC
            """)
    Optional<Restaurant> findByNameAndAddress(String name, String address);

    @Query("""
            SELECT r from Restaurant r
            JOIN FETCH r.menuItems mi
            JOIN FETCH mi.dishRef d
            WHERE r.id=:id AND r.enabled=true AND mi.actualDate=:date AND d.enabled=true
            ORDER BY d.name ASC
            """)
    Optional<Restaurant> findWithMenuByRestaurantAndDate(int id, LocalDate date);

    // https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#hql-distinct
    // https://stackoverflow.com/questions/1346181/how-does-distinct-work-when-using-jpa-and-hibernate#53406102
    @QueryHints({
            @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false")
    })
    @Query("""
            SELECT DISTINCT r from Restaurant r
            JOIN FETCH r.menuItems mi
            JOIN FETCH mi.dishRef d
            WHERE r.enabled=true AND mi.actualDate=:date AND d.enabled=true
            ORDER BY r.name ASC, d.name ASC
            """)
    List<Restaurant> getWithMenuByDate(LocalDate date);

    @Query("SELECT r from Restaurant r WHERE r.enabled=true ORDER BY r.name ASC")
    List<Restaurant> getAllEnabled();

    @Query("SELECT r from Restaurant r WHERE r.id IN (:ids) ORDER BY r.name ASC")
    List<Restaurant> getIn(Collection<Integer> ids);

    default void checkAvailable(int id) {
        if (!getById(id).isEnabled()) {
            throw new DataDisabledException("Restaurant " + id + " is unavailable");
        }
    }
}