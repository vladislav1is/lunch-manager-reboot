package com.redfox.restaurantvoting.web.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.util.validation.NoHtml;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;

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
    public ResponseEntity<User> createWithLocation(@RequestParam @NotBlank @Size(min = 2, max = 100) @NoHtml String name,
                                                   @RequestParam @NotBlank @Size(max = 100) @Email @NoHtml String email,
                                                   @RequestParam @NotBlank @Size(min = 5, max = 100) String password,
                                                   @RequestParam Role role) {
        User user = new User(null, name, email, password, true, now().truncatedTo(MINUTES), getRoles(role));
        User created = super.create(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/admin/users" + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    private EnumSet<Role> getRoles(Role role) {
        EnumSet<Role> roles;
        switch (role) {
            case R_ADMIN -> roles = EnumSet.of(Role.USER, Role.R_ADMIN);
            case ADMIN -> roles = EnumSet.of(Role.USER, Role.ADMIN);
            default -> roles = EnumSet.of(Role.USER);
        }
        return roles;
    }
}
