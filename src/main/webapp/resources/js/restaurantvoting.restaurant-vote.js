const restaurantAjaxUrl = "profile/restaurants/" + restaurantId + "/";

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
                "data": function (data, type, row) {
                    if (type === "display") {
                        return '<div id="votesCount">' + votesCount + '</div>'
                    }
                    return votesCount;
                }
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