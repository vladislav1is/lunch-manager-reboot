package com.redfox.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.redfox.restaurantvoting.HasRestaurantConstraint;
import com.redfox.restaurantvoting.mapper.Default;
import com.redfox.restaurantvoting.util.validation.NoHtml;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "restaurant", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "address"}, name = "restaurants_uk")})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true, exclude = "menuItems")
public class Restaurant extends NamedEntity implements HasRestaurantConstraint {
    @Column(name = "address")
    @Size(max = 1024)
    @NoHtml
    @Nullable
    private String address;

    @Column(name = "enabled", nullable = false, columnDefinition = "bool default true")
    private boolean enabled = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "restaurant")
    @OnDelete(action = OnDeleteAction.CASCADE)  // https://stackoverflow.com/a/44988100/548473
    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_EMPTY) // https://stackoverflow.com/a/27964775/548473
    private List<MenuItem> menuItems;

    @Default
    public Restaurant(Integer id, String name, @Nullable String address) {
        super(id, name);
        this.address = address;
    }

    public Restaurant(Integer id, String name, @Nullable String address, boolean enabled, List<MenuItem> menuItems) {
        super(id, name);
        this.address = address;
        this.enabled = enabled;
        this.menuItems = menuItems;
    }

    public Restaurant(Restaurant r) {
        this(r.id, r.name, r.address, r.enabled, r.menuItems);
    }
}