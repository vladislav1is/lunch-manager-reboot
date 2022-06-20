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
                "orderable": false,
                "defaultContent": "",
                "render": renderMenuBtn
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