package com.redfox.restaurantvoting.web.user;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static com.redfox.restaurantvoting.util.validation.Validations.assureIdConsistent;
import static com.redfox.restaurantvoting.util.validation.Validations.checkNew;

@RestController
@RequestMapping(value = AdminUserController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
//  TODO: cache only most requested data!
@CacheConfig(cacheNames = "users")
public class AdminUserController extends AbstractUserController {
    static final String REST_URL = "/api/admin/users";

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable int id) {
        log.info("get {}", id);
        return ResponseEntity.of(repository.getExisted(id));
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @GetMapping
    @Cacheable
    public List<User> getAll() {
        log.info("getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @JsonView(View.UserWithoutRestaurants.class)
    @CacheEvict(allEntries = true)
    public ResponseEntity<User> createWithLocation(@RequestBody @Valid User user) {
        log.info("create {}", user);
        checkNew(user);
        User created = prepareAndSave(user);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(allEntries = true)
    public void update(@RequestBody @Valid User user, @PathVariable int id) {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        prepareAndSave(user);
    }

    @GetMapping("/by-email")
    public ResponseEntity<User> getByEmail(@RequestParam String email) {
        log.info("getByEmail {}", email);
        return ResponseEntity.of(repository.getExistedEmail(email));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    @CacheEvict(allEntries = true)
    public void enable(@PathVariable int id, @RequestParam boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        User user = repository.getById(id);
        user.setEnabled(enabled);
    }
}