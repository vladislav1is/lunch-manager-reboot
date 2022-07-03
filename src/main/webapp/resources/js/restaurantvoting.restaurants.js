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
            },
            {
                "orderable": false,
                "defaultContent": "",
                "render": renderVoteBtn
            }
        ],
        "order":
            [
                [
                    0,
                    "asc"
                ]
            ],
        "createdRow":
            function (row, data) {
                if (data.voted === true) {
                    $(row).attr("class", "data-voted font-weight-bold")
                }
            }
    });
});