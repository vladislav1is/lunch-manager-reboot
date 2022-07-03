package com.redfox.restaurantvoting.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.UserRepository;
import com.redfox.restaurantvoting.util.validation.AdminRestaurantsUtil;
import com.redfox.restaurantvoting.web.AuthUser;
import com.redfox.restaurantvoting.web.GlobalExceptionHandler;
import com.redfox.restaurantvoting.web.SecurityUtil;
import com.redfox.restaurantvoting.web.restaurant.AdminRestaurantController;
import com.redfox.restaurantvoting.web.restaurant.AdminRestaurantUIController;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.DispatcherServletWebRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static com.redfox.restaurantvoting.util.Users.PASSWORD_ENCODER;

@EnableWebSecurity()
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@Slf4j
@AllArgsConstructor
public class WebSecurityConfig {

    private final UserRepository userRepository;

    private final GlobalExceptionHandler globalExceptionHandler;

    @Bean
    //  https://stackoverflow.com/a/70176629/548473
    public UserDetailsService userDetailsServiceBean() {
        return userDetailsService();
    }

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
                    try {
                        AdminRestaurantsUtil.checkRequestAccess(authUser.getUser(), request.getRequestURI());
                    } catch (AccessDeniedException exception) {
                        ResponseEntity<?> responseEntity = globalExceptionHandler.accessDeniedException(new DispatcherServletWebRequest(request, response), exception);

                        response.setStatus(responseEntity.getStatusCode().value());
                        response.setContentType("application/json");
                        response.setLocale(request.getLocale());

                        ObjectMapper mapper = new ObjectMapper();
                        PrintWriter out = response.getWriter();
                        out.print(mapper.writeValueAsString(responseEntity.getBody()));
                        out.flush();
                        return;
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Configuration
    @Order(1)
    // https://stackoverflow.com/questions/33603156/spring-security-multiple-http-config-not-working#33608459
    public static class ApiWebSecurityConfigAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/**").authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/api/restaurants/vote-today").hasRole(Role.USER.name())
                    .antMatchers("/api/restaurants/**").permitAll()
                    .antMatchers(AdminRestaurantController.REST_URL + "/**").hasAnyRole(Role.R_ADMIN.name(), Role.ADMIN.name())
                    .antMatchers(HttpMethod.POST, "/api/profile").anonymous()
                    .antMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                    .anyRequest().authenticated()
                    .and().httpBasic()
                    .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and().csrf().disable();
        }
    }

    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/register").anonymous()
                    .antMatchers("/login").permitAll()
                    .antMatchers("/resources/**").permitAll()
                    .antMatchers("/webjars/**").permitAll()
                    .antMatchers("/swagger-ui/**").permitAll()
                    .antMatchers("/v3/api-docs/**").permitAll()
                    .antMatchers("/profile/**").hasRole(Role.USER.name())
                    .antMatchers(AdminRestaurantUIController.REST_URL + "/**").hasAnyRole(Role.R_ADMIN.name(), Role.ADMIN.name())
                    .antMatchers("/admin/**").hasRole(Role.ADMIN.name())
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/restaurants", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutSuccessUrl("/login?logout=true")
                    .deleteCookies("JSESSIONID")
                    .permitAll();
        }
    }
}