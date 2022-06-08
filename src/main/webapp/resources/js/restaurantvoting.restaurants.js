const restaurantAjaxUrl = "profile/restaurants/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: restaurantAjaxUrl,
    updateTable: function () {
        $.get(restaurantAjaxUrl, updateTableByData);
    }
}

$(function () {
    makeEditable({
        "columns": [
            {
                "data": "name"
            },
            {
                "data": "address"
            }
        ],
        "order":
            [
                [
                    0,
                    "asc"
                ]
            ]
    });
});