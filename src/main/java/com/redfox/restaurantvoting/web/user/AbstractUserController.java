package com.redfox.restaurantvoting.web.user;

import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@Slf4j
public abstract class AbstractUserController {

    @Autowired
    protected UserRepository repository;

    public ResponseEntity<User> get(int id) {
        log.info("get {}", id);
        return ResponseEntity.of(repository.findById(id));
    }

    public void delete(int id) {
        log.info("delete {}", id);
        repository.deleteExisted(id);
    }
}