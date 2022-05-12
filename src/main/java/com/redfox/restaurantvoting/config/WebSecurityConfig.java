package com.redfox.restaurantvoting.config;

import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.UserRepository;
import com.redfox.restaurantvoting.util.validation.AdminRestaurantsUtil;
import com.redfox.restaurantvoting.web.AuthUser;
import com.redfox.restaurantvoting.web.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.redfox.restaurantvoting.util.Users.PASSWORD_ENCODER;

@EnableWebSecurity()
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@Slf4j
@AllArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    @Bean
    @Override
    //  https://stackoverflow.com/a/70176629/548473
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return email -> {
            log.debug("Authenticating '{}'", email);
            Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(email);
            return new AuthUser(optionalUser.orElseThrow(
                    () -> new UsernameNotFoundException("User '" + email + "' was not found")));
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(PASSWORD_ENCODER);
    }

    @Bean
    public OncePerRequestFilter restaurantsAdminFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
                AuthUser authUser = SecurityUtil.safeGet();
                if (authUser != null && authUser.hasRole(Role.R_ADMIN)) {
                    AdminRestaurantsUtil.checkRequestAccess(authUser.getUser(), request.getRequestURI());
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/restaurants").permitAll()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/swagger-ui/**").permitAll()
                .antMatchers("/v3/api-docs/**").permitAll()
                .antMatchers("/api/restaurants/**").permitAll()
                .antMatchers(AdminRestaurantsUtil.REST_URL + "/**").hasAnyRole(Role.R_ADMIN.name(), Role.ADMIN.name())
                .antMatchers(HttpMethod.POST, "/api/profile").anonymous()
                .antMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/restaurants")
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl("/login?logout=true")
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and().httpBasic()
                // .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable();
    }
}