package com.redfox.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.HasIdAndEmail;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.mapper.Default;
import com.redfox.restaurantvoting.util.validation.NoHtml;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static java.time.LocalDateTime.now;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "email", name = "users_uk")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = {"password"})
public class User extends NamedEntity implements HasIdAndEmail, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @NotBlank
    @Size(max = 100)
    @NoHtml // https://stackoverflow.com/questions/17480809
    @JsonView(View.UserWithoutRestaurants.class)
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(min = 5, max = 100)
    // https://stackoverflow.com/a/12505165/548473
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonView(View.UserWithoutRestaurants.class)
    private String password;

    @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
    @JsonView(View.UserWithoutRestaurants.class)
    private boolean enabled = true;

    @Column(name = "registered", nullable = false, columnDefinition = "timestamp default now()", updatable = false)
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonView(View.UserWithoutRestaurants.class)
    private LocalDateTime registered = now().truncatedTo(ChronoUnit.MINUTES);

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "role"}, name = "user_roles_ui")})
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id") // https://stackoverflow.com/a/62848296/548473
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy(value = "role DESC")
    @JsonView(View.UserWithoutRestaurants.class)
    private Set<Role> roles;

    @CollectionTable(name = "admin_restaurant",
            joinColumns = @JoinColumn(name = "admin_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"admin_id", "restaurant_id"}, name = "admin_restaurant_uk"))
    @Column(name = "restaurant_id")
    @ElementCollection(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Schema(hidden = true)
    private Set<Integer> adminRestaurants = Set.of();

    public User(Integer id, String name, String email, String password, Role role, Role... roles) {
        this(id, name, email, password, true, now().truncatedTo(ChronoUnit.MINUTES), EnumSet.of(role, roles));
    }

    public User(Integer id, String name, String email, String password, boolean enabled, LocalDateTime registered, Collection<Role> roles) {
        super(id, name);
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.registered = registered;
        setRoles(roles);
    }

    public User(User u) {
        this(u.id, u.name, u.email, u.password, u.enabled, u.registered, u.roles);
    }

    @Default
    public User(Integer id, String name, String email, String password) {
        this(id, name, email, password, true, now().truncatedTo(ChronoUnit.MINUTES), EnumSet.of(Role.USER));
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = CollectionUtils.isEmpty(roles) ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles);
    }

    public void setRole(Role role) {
        switch (role) {
            case R_ADMIN -> this.roles = EnumSet.of(Role.USER, Role.R_ADMIN);
            case ADMIN -> this.roles = EnumSet.of(Role.USER, Role.ADMIN);
            default -> this.roles = EnumSet.of(Role.USER);
        }
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
}