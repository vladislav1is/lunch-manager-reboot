package com.redfox.restaurantvoting.to;

import com.redfox.restaurantvoting.HasRestaurantConstraint;
import com.redfox.restaurantvoting.util.validation.NoHtml;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantWithVote extends NamedTo implements HasRestaurantConstraint {

    public RestaurantWithVote(Integer id, String name, String address, boolean voted) {
        super(id, name);
        this.address = address;
        this.voted = voted;
    }

    @Size(max = 1024)
    @NoHtml
    @NotBlank
    private String address;

    private boolean voted;
}