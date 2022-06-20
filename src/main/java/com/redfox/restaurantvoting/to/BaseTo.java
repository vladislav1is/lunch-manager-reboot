package com.redfox.restaurantvoting.to;

import com.fasterxml.jackson.annotation.JsonView;
import com.redfox.restaurantvoting.HasId;
import com.redfox.restaurantvoting.View;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseTo implements HasId {
    @Schema(accessMode = Schema.AccessMode.READ_ONLY) // https://stackoverflow.com/a/28025008/548473
    @JsonView(View.MenuItemWithoutEnabled.class)
    protected Integer id;

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + id;
    }
}
