package com.redfox.restaurantvoting.web.user;

import com.redfox.restaurantvoting.mapper.UserMapper;
import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.to.UserTo;
import com.redfox.restaurantvoting.web.AuthUser;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.EnumSet;

@Hidden
@Controller
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class ProfileUserUIController extends AbstractUserController {

    private final UserMapper userMapper;

    @GetMapping("/profile")
    public String profile(ModelMap model, @AuthenticationPrincipal AuthUser authUser) {
        UserTo userTo = userMapper.toTo(authUser.getUser());
        userTo.setPassword(null);
        model.addAttribute("userTo", userTo);
        return "profile";
    }

    @PostMapping("/profile")
    @CachePut(key = "#authUser.username")
    public String update(@Valid UserTo userTo, BindingResult result, SessionStatus status, @AuthenticationPrincipal AuthUser authUser) {
        if (result.hasErrors()) {
            return "profile";
        }
        User updated = userMapper.updateFromTo(authUser.getUser(), userTo);
        super.update(updated, authUser.id());
        status.setComplete();
        return "redirect:/restaurants";
    }

    @GetMapping("/register")
    public String register(Model model) {
        UserTo userTo = new UserTo(null, null, null, null);
        model.addAttribute("userTo", userTo);
        return "profile";
    }

    @PostMapping("/register")
    @CacheEvict(allEntries = true)
    public String save(@Valid UserTo userTo, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "profile";
        }
        User user = new User(null, null, null, null, Boolean.TRUE, LocalDateTime.now(), EnumSet.of(Role.USER));
        User created = userMapper.updateFromTo(user, userTo);
        super.create(created);
        status.setComplete();
        return "redirect:/login?register=true&username=" + created.getEmail();
    }
}
