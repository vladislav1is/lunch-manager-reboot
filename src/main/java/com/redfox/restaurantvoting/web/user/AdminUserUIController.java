package com.redfox.restaurantvoting.web.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.model.User;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Hidden
@RestController
@RequestMapping(value = AdminUserUIController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminUserUIController extends AbstractUserController {
    static final String REST_URL = "/admin/users";

    @Override
    @GetMapping
    public List<User> getAll() {
        return super.getAll();
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @JsonView(View.UserWithoutRestaurants.class)
    public ResponseEntity<User> createWithLocation(@Valid User user) {
        User created = super.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/admin/users" + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @Override
    @PostMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        super.enable(id, enabled);
    }
}
