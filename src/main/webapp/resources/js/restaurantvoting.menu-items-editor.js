const menuItemAjaxUrl = "admin/restaurants/" + restaurantId + "/menu-items/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: menuItemAjaxUrl,
    updateTable: function () {
        $.ajax({
            type: "GET",
            url: menuItemAjaxUrl + "filter",
            data: $("#filter").serialize()
        }).done(updateTableByData);
    }
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get(menuItemAjaxUrl, updateTableByData);
}

$(function () {
    makeEditable({
        "columns": [
            {
                "data": "actualDate"
            },
            {
                "data": "name"
            },
            {
                "data": "enabled",
                "render": renderEnableBtns

            },
            {
                "data": "price"
            },
            {
                "orderable": false,
                "defaultContent": "",
                "render": renderEditBtn
            },
            {
                "orderable": false,
                "defaultContent": "",
                "render": renderDeleteBtn
            }
        ],
        "order": [
            [
                0,
                "desc"
            ]
        ],
        "createdRow": function (row, data, dataIndex) {
            if (!data.enabled) {
                $(row).attr("data-entity-enabled", false);
            }
        }
    });

    $.datetimepicker.setLocale(localeCode);

    //  http://xdsoft.net/jqplugins/datetimepicker/
    let startDate = $('#startDate');
    let endDate = $('#endDate');
    startDate.datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        formatDate: 'Y-m-d',
        onShow: function (ct) {
            this.setOptions({
                maxDate: endDate.val() ? endDate.val() : false
            })
        }
    });
    endDate.datetimepicker({
        timepicker: false,
        format: 'Y-m-d',
        formatDate: 'Y-m-d',
        onShow: function (ct) {
            this.setOptions({
                minDate: startDate.val() ? startDate.val() : false
            })
        }
    });

    $('#actualDate').datetimepicker({
        timepicker: false,
        format: 'Y-m-d'
    });
});