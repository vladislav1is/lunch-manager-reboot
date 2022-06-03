package com.redfox.restaurantvoting.web.user;

import com.redfox.restaurantvoting.mapper.UserMapper;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.to.UserTo;
import com.redfox.restaurantvoting.web.SecurityUtil;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;

@Hidden
@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileUserUIController extends AbstractUserController {

    private final UserMapper userMapper;

    @GetMapping
    public String profile() {
        return "profile";
    }

    @PostMapping
    @CacheEvict(cacheNames = "users", allEntries = true)
    public String update(@Valid UserTo userTo, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "profile";
        }
        User user = SecurityUtil.authUser();
        User created = userMapper.updateFromTo(user, userTo);
        super.update(created, SecurityUtil.authId());
        status.setComplete();
        return "redirect:/restaurants";
    }
}
