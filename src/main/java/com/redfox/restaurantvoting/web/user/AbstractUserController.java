package com.redfox.restaurantvoting.web.user;

import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.UserRepository;
import com.redfox.restaurantvoting.util.Users;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.util.List;
import java.util.Optional;

import static com.redfox.restaurantvoting.util.validation.Validations.assureIdConsistent;
import static com.redfox.restaurantvoting.util.validation.Validations.checkNew;

@Slf4j
@CacheConfig(cacheNames = "users")
public abstract class AbstractUserController {

    @Autowired
    protected UserRepository repository;

    @Autowired
    private UniqueMailValidator emailValidator;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }

    public Optional<User> findById(int id) {
        log.info("get {}", id);
        return repository.getExisted(id);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        log.info("delete {}", id);
        repository.deleteExisted(id);
    }

    @Cacheable
    public List<User> getAll() {
        log.info("getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"));
    }

    public User create(User user) {
        log.info("create {}", user);
        checkNew(user);
        return prepareAndSave(user);
    }

    protected User prepareAndSave(User user) {
        return repository.save(Users.prepareToSave(user));
    }

    public void update(User user, int id) {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        prepareAndSave(user);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void enable(int id, boolean enabled) {
        log.info(enabled ? "enable {}" : "disable {}", id);
        User user = repository.getById(id);
        user.setEnabled(enabled);
    }
}