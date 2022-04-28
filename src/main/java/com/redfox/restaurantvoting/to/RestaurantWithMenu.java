package com.redfox.restaurantvoting.to;

import com.redfox.restaurantvoting.model.DishRef;
import com.redfox.restaurantvoting.util.validation.NoHtml;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Size;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RestaurantWithMenu extends NamedTo {

    public RestaurantWithMenu(Integer id, String name, @Nullable String address, List<DishRef> dishRefs) {
        super(id, name);
        this.address = address;
        this.dishRefs = dishRefs;
    }

    @Size(max = 1024)
    @NoHtml
    @Nullable
    String address;

    List<DishRef> dishRefs;
}