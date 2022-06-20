const dishAjaxUrl = "profile/restaurants/" + restaurantId + "/menu-items/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: dishAjaxUrl,
    updateTable: function () {
        $.get(dishAjaxUrl, updateTableByData);
    }
}

$(function () {
    makeEditable({
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "price"
            },
            {
                "data": "actualDate"
            }
        ],
        "order": [
            [
                0,
                "desc"
            ]
        ]
    });
});