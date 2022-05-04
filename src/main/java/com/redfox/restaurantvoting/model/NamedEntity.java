package com.redfox.restaurantvoting.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.View;
import com.redfox.restaurantvoting.util.validation.NoHtml;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NamedEntity extends BaseEntity {
    @Column(name = "name", nullable = false)
    @NotBlank
    @Size(min = 2, max = 100)
    @NoHtml
    @JsonView(View.UserWithoutRestaurants.class)
    protected String name;

    protected NamedEntity(Integer id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString() + '[' + name + ']';
    }
}
