package com.redfox.restaurantvoting.config;

import com.redfox.restaurantvoting.error.DataDisabledException;
import com.redfox.restaurantvoting.model.Role;
import com.redfox.restaurantvoting.model.User;
import com.redfox.restaurantvoting.repository.UserRepository;
import com.redfox.restaurantvoting.util.JsonUtil;
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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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

import static com.redfox.restaurantvoting.util.UserUtil.PASSWORD_ENCODER;

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
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                if (!user.isEnabled()) {
                    throw new DataDisabledException("User '" + email + "' is disabled");
                } else {
                    return new AuthUser(user);
                }
            } else {
                throw new UsernameNotFoundException("User '" + email + "' not found");
            }
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
                try {
                    AuthUser authUser = SecurityUtil.safeGet();
                    if (authUser != null) {
                        checkAuthUserAccess(request, authUser);
                    }
                    filterChain.doFilter(request, response);
                } catch (UsernameNotFoundException usernameNotFoundException) {
                    ResponseEntity<?> responseEntity = globalExceptionHandler.usernameNotFoundException(
                            new DispatcherServletWebRequest(request, response), usernameNotFoundException);
                    filterExceptionHandler(request, response, responseEntity);
                } catch (DataDisabledException dataDisabledException) {
                    ResponseEntity<?> responseEntity = globalExceptionHandler.dataDisabledException(
                            new DispatcherServletWebRequest(request, response), dataDisabledException);
                    filterExceptionHandler(request, response, responseEntity);
                } catch (AuthenticationException authenticationException) {
                    ResponseEntity<?> responseEntity = globalExceptionHandler.authenticationException(
                            new DispatcherServletWebRequest(request, response), authenticationException);
                    filterExceptionHandler(request, response, responseEntity);
                } catch (AccessDeniedException accessDeniedException) {
                    ResponseEntity<?> responseEntity = globalExceptionHandler.accessDeniedException(
                            new DispatcherServletWebRequest(request, response), accessDeniedException);
                    filterExceptionHandler(request, response, responseEntity);
                }
            }
        };
    }

    private void checkAuthUserAccess(HttpServletRequest request, AuthUser authUser) {
        if (authUser.hasRole(Role.USER)) {
            Optional<User> dbUser = userRepository.findById(authUser.id());
            User currentUser = authUser.getUser();
            if (dbUser.isPresent()) {
                User updated = dbUser.get();
                updateAuthUser(updated, authUser);
                if (authUser.hasRole(Role.R_ADMIN)) {
                    AdminRestaurantsUtil.checkRequestAccess(currentUser, request.getRequestURI());
                }
            } else {
                throw new UsernameNotFoundException("User '" + currentUser.getEmail() + "' not found");
            }
        }
    }

    private void updateAuthUser(User updated, AuthUser authUser) {
        User current = authUser.getUser();
        String email = current.getEmail();
        if (!updated.isEnabled()) {
            throw new DataDisabledException("User '" + email + "' is disabled");
        } else if (!current.getRoles().equals(updated.getRoles())) {
            throw new AuthenticationException("User '" + email + " has new roles") {
                @Override
                public String getMessage() {
                    return super.getMessage();
                }
            };
        } else {
            authUser.setUser(updated);
        }
    }

    private void filterExceptionHandler(HttpServletRequest request, HttpServletResponse response, ResponseEntity<?> responseEntity) throws IOException {
        String servletPath = request.getServletPath().split("/")[1];
        SecurityContextHolder.clearContext();
        if (servletPath.equals("api")) {
            response.setStatus(responseEntity.getStatusCode().value());
            response.setContentType("application/json");

            PrintWriter out = response.getWriter();
            out.print(JsonUtil.writeValue(responseEntity.getBody()));
            out.flush();
            out.close();
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
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