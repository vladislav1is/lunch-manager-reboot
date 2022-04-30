package com.redfox.restaurantvoting.web.restaurant;

import com.redfox.restaurantvoting.model.DishRef;
import com.redfox.restaurantvoting.model.MenuItem;
import com.redfox.restaurantvoting.model.Restaurant;
import com.redfox.restaurantvoting.to.RestaurantWithMenu;
import com.redfox.restaurantvoting.web.MatcherFactory;
import com.redfox.restaurantvoting.web.MatcherFactory.Matcher;

import java.time.LocalDate;
import java.util.List;

public class RestaurantTestData {
    public static final Matcher<Restaurant> RESTAURANT_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Restaurant.class, "menuItems");
    public static final Matcher<RestaurantWithMenu> RESTAURANT_MATCHER_WITH_MENU = MatcherFactory.usingIgnoringFieldsComparator(RestaurantWithMenu.class, "dishRefs.restaurant");

    public static final Matcher<MenuItem> MENUITEM_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(MenuItem.class, "restaurant", "dishRef");

    public static final int YAKITORIYA_ID = 1;
    public static final int DODO_ID = 2;
    public static final int MAC_ID = 3;
    public static final int TEREMOK_ID = 4;
    public static final int STARBUCKS_ID = 5;

    public static final Restaurant yakitoriya = new Restaurant(YAKITORIYA_ID, "Якитория", "Балаклавский просп., 14А");
    public static final Restaurant dodo = new Restaurant(DODO_ID, "Додо Пицца", "ул. Намёткина, 13Б");
    public static final Restaurant mac = new Restaurant(MAC_ID, "McDonalds", "Газетный пер., 17");
    public static final Restaurant teremok = new Restaurant(TEREMOK_ID, "Теремок", "Гоголевский бул., 3");
    public static final Restaurant starbucks = new Restaurant(STARBUCKS_ID, "Starbucks", "ул. Арбат, 19");

    public static final DishRef yakitoriya_c = new DishRef(1, "Калифорния", 55700, yakitoriya);
    public static final DishRef yakitoriya_rsc = new DishRef(2, "Ролл Лосось-карамель", 49900, yakitoriya);

    public static final DishRef dodo_c = new DishRef(3, "Карбонара", 71900, dodo);
    public static final DishRef dodo_p = new DishRef(4, "Пепперони", 64900, dodo);

    public static final DishRef mac_ff = new DishRef(5, "Картофель Фри", 12800, mac);
    public static final DishRef mac_fof = new DishRef(6, "Филе-о-Фиш", 12700, mac);
    public static final DishRef mac_dc = new DishRef(7, "Двойной Чизбургер", 12500, mac);
    public static final DishRef mac_c = new DishRef(8, "Чикенбургер", 5300, mac);

    public static final DishRef teremok_bwd = new DishRef(9, "Борщ с уткой", 27600, teremok);
    public static final DishRef teremok_pc = new DishRef(10, "Блин Карбонара", 31800, teremok);
    public static final DishRef teremok_pcm = new DishRef(11, "Блин Сгущёнка", 22100, teremok);

    public static final DishRef starbucks_cs = new DishRef(12, "Капучино Шорт", 30000, starbucks);
    public static final DishRef starbucks_ed = new DishRef(13, "Эспрессо Доппио", 15500, starbucks);
    public static final DishRef starbucks_acc = new DishRef(14, "Американо Кон Крема", 39000, starbucks);

    public static final MenuItem yakitoriya_1 = new MenuItem(1, LocalDate.now(), yakitoriya, yakitoriya_c);
    public static final MenuItem yakitoriya_2 = new MenuItem(2, LocalDate.now(), yakitoriya, yakitoriya_rsc);

    public static final MenuItem dodo_3 = new MenuItem(3, LocalDate.now(), dodo, dodo_c);
    public static final MenuItem dodo_4 = new MenuItem(4, LocalDate.now(), dodo, dodo_p);

    public static final MenuItem mac_5 = new MenuItem(5, LocalDate.now(), mac, mac_ff);
    public static final MenuItem mac_6 = new MenuItem(6, LocalDate.now(), mac, mac_fof);
    public static final MenuItem mac_7 = new MenuItem(7, LocalDate.now(), mac, mac_dc);
    public static final MenuItem mac_8 = new MenuItem(8, LocalDate.now(), mac, mac_c);

    public static final MenuItem teremok_9 = new MenuItem(9, LocalDate.now(), teremok, teremok_bwd);
    public static final MenuItem teremok_10 = new MenuItem(10, LocalDate.now(), teremok, teremok_pc);
    public static final MenuItem teremok_11 = new MenuItem(11, LocalDate.now(), teremok, teremok_pcm);

    public static final MenuItem starbucks_12 = new MenuItem(12, LocalDate.now(), starbucks, starbucks_cs);
    public static final MenuItem starbucks_13 = new MenuItem(13, LocalDate.now(), starbucks, starbucks_ed);
    public static final MenuItem starbucks_14 = new MenuItem(14, LocalDate.now(), starbucks, starbucks_acc);

    // set menu for today, sorted by restaurant name & dish name
    static {
        mac_fof.setEnabled(false);
        yakitoriya.setMenuItems(List.of(yakitoriya_1, yakitoriya_2));
        dodo.setMenuItems(List.of(dodo_3, dodo_4));
        mac.setMenuItems(List.of(mac_5, mac_6, mac_7, mac_8));
        mac.setEnabled(false);
        teremok.setMenuItems(List.of(teremok_10, teremok_11, teremok_9));
        starbucks.setMenuItems(List.of(starbucks_12, starbucks_13, starbucks_14));
        starbucks.setEnabled(false);
    }

    public static Restaurant getNew() {
        return new Restaurant(null, "Новый", "не определен");
    }

    public static Restaurant getUpdated() {
        return new Restaurant(YAKITORIYA_ID, "Якитория", "Верейская улица, 17");
    }

    public static MenuItem getNewMenuItem() {
        return new MenuItem(null, LocalDate.now(), yakitoriya, yakitoriya_c);
    }

    public static MenuItem getUpdatedMenuItem() {
        return new MenuItem(yakitoriya_1.id(), LocalDate.now(), getUpdated(), getUpdatedDish());
    }

    public static DishRef getNewDish() {
        return new DishRef(null, "Новая Якитория-еда", 12000, yakitoriya);
    }

    public static DishRef getUpdatedDish() {
        return new DishRef(yakitoriya_c.id(), "Калифорния-2", 17500, yakitoriya);
    }
}
