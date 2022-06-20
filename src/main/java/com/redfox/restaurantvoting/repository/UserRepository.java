package com.redfox.restaurantvoting.repository;

import com.redfox.restaurantvoting.error.DataDisabledException;
import com.redfox.restaurantvoting.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.redfox.restaurantvoting.util.validation.Validations.checkModification;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(String email);

    default Optional<User> getExistedByEmail(String email) {
        Optional<User> bean = findByEmailIgnoreCase(email);
        checkModification(bean.isEmpty(), email);
        return bean;
    }

    default void checkAvailable(int id) {
        if (!getById(id).isEnabled()) {
            throw new DataDisabledException("User " + id + " is unavailable");
        }
    }
}