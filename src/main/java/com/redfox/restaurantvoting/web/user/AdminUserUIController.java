package com.redfox.restaurantvoting.web.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.model.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Hidden
@RestController
@RequestMapping(value = AdminUserUIController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserUIController extends AbstractUserController {
    static final String REST_URL = "/admin/users";

    @GetMapping("/{id}")
    @JsonView(View.UserWithoutRestaurants.class)
    public ResponseEntity<User> get(@PathVariable int id) {
        return ResponseEntity.of(super.findById(id));
    }

    @Override
    @GetMapping
    @JsonView(View.UserWithoutRestaurants.class)
    public List<User> getAll() {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(cacheNames = "users", allEntries = true)
    public void createOrUpdate(@Valid User user) {
        if (user.isNew()) {
            super.create(user);
        } else {
            super.update(user, user.id());
        }
    }

    @Override
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        super.enable(id, enabled);
    }
}
