let form;

function makeEditable(datatableOpts) {
    ctx.datatableApi = $("#datatable").DataTable(
        //  https://api.jquery.com/jquery.extend/#jQuery-extend-deep-target-object1-objectN
        $.extend(true, datatableOpts,
            {
                "ajax": {
                    "url": ctx.ajaxUrl,
                    "dataSrc": ""
                },
                "retrieve": true,
                "paging": false,
                "info": true,
                "language": {
                    "search": i18n["common.search"],
                    "info": i18n["common.info"],
                    "infoEmpty": i18n["common.infoEmpty"],
                    "emptyTable": i18n["common.emptyTable"]
                }
            }
        ));
    form = $('#detailsForm');

    $(document).ajaxError(function (event, jqXHR, options, jsExc) {
        failNoty(jqXHR);
    });

    // solve problem with cache in IE: https://stackoverflow.com/a/4303862/548473
    $.ajaxSetup({cache: false});

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
}

function enable(chkbox, id, isGroup) {
    let enabled = chkbox.is(":checked");
    //  https://stackoverflow.com/a/22213543/548473
    $.ajax({
        url: ctx.ajaxUrl + id,
        type: "POST",
        data: "enabled=" + enabled
    }).done(function () {
        if (isGroup) {
            ctx.updateTable();
            successNoty(enabled ? "common.group.enabled" : "common.group.disabled");
        } else {
            chkbox.closest("tr").attr("data-entity-enabled", enabled);
            successNoty(enabled ? "common.enabled" : "common.disabled");
        }
    }).fail(function () {
        $(chkbox).prop("checked", !enabled);
    });
}

function renderEnableBtn(data, type, row) {
    if (type === "display") {
        return "<input type='checkbox' " + (data ? "checked" : "") + " onclick='enable($(this), " + row.id + ", " + false + ");'/>";
    }
    return data;
}

function renderEnableBtns(data, type, row) {
    if (type === "display") {
        return "<input type='checkbox' " + (data ? "checked" : "") + " onclick='enable($(this), " + row.id + ", " + true + ");'/>";
    }
    return data;
}

function add() {
    $("#modalTitle").html(i18n["addTitle"]);
    form.find(":input").val("");
    form.find('#role').val("USER");
    $("#editRow").modal();
}

function updateRow(id) {
    $("#modalTitle").html(i18n["editTitle"]);
    form.find(":input").val("");
    $.get(ctx.ajaxUrl + id, function (data) {
        $.each(data, function (key, value) {
            if (Object.is("roles", key)) {
                switch (value[1]) {
                    case "R_ADMIN":
                        form.find('#role').val("R_ADMIN");
                        break;
                    case "ADMIN":
                        form.find('#role').val("ADMIN");
                        break;
                    default:
                        form.find('#role').val("USER");
                }
            } else {
                form.find("input[name='" + key + "']").val(value);
            }
        });
        $('#editRow').modal();
    });
}

function deleteRow(id) {
    if (confirm(i18n['common.confirm'])) {
        $.ajax({
            url: ctx.ajaxUrl + id,
            type: "DELETE"
        }).done(function () {
            ctx.updateTable();
            successNoty("common.deleted");
        });
    }
}

function updateTableByData(data) {
    ctx.datatableApi.clear().rows.add(data).draw();
}

function save() {
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl,
        data: form.serialize()
    }).done(function () {
        $("#editRow").modal("hide");
        ctx.updateTable();
        successNoty("common.saved");
    });
}

function saveMenuItem() {
    if (Object.is($('#price').val(), "")) {
        form.find('#price').val(0);
    }
    save();
}

function vote() {
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl + "vote"
    }).done(function () {
        updateVisitors();
        successNoty("common.saved");
    });
}

function updateVisitors() {
    $.ajax({
        type: "GET",
        url: ctx.ajaxUrl + "count-visitors",
        data: {
            "restaurantId": restaurantId
        },
        success: function (data) {
            $('#votesCount').text(data.visitors);
        },
    });
}

function deleteVote() {
    if (confirm(i18n['common.confirm'])) {
        $.ajax({
            type: "DELETE",
            url: "profile/restaurants/vote",
            data: {
                "deleteVote": true
            }
        }).done(function () {
            updateVisitors();
            successNoty("common.deleted");
        });
    }
}

function renderEditBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='updateRow(" + row.id + ");'><span class='fa text-dark fa-pencil'></span></a>";
    }
}

function renderDeleteBtn(data, type, row) {
    if (type === "display") {
        return "<a onclick='deleteRow(" + row.id + ");'><span class='fa text-dark fa-remove'></span></a>";
    }
}

function renderEditMenuBtn(data, type, row) {
    if (type === "display") {
        return "<button type='button' onclick=\"window.location.href='restaurants/" + row.id + "/menu-items/editor'\"\n" +
            " class='btn btn-sm btn-secondary'>" + i18n["common.edit"] + "</button>";
    }
}

function renderMenuBtn(data, type, row) {
    if (type === "display") {
        return "<button type='button' onclick=\"window.location.href='restaurants/" + row.id + "/menu-items'\"\n" +
            " class='btn btn-sm btn-secondary'>" + i18n["common.view"] + "</button>";
    }
}

function renderVoteBtn(data, type, row) {
    if (type === "display") {
        return "<button type='button' onclick=\"window.location.href='restaurants/" + row.id + "/vote'\"\n" +
            " class='btn btn-sm btn-secondary'>" +
            "<span class='fa text-light fa-plus'></span>&nbsp&nbsp<span class='fa text-white-50 fa-users'></span>" +
            "</button>";
    }
}

let failedNote;

function closeNoty() {
    if (failedNote) {
        failedNote.close();
        failedNote = undefined;
    }
}

function successNoty(key) {
    closeNoty();
    new Noty({
        text: "<span class='fa fa-lg fa-check'></span> &nbsp;" + i18n[key],
        type: 'success',
        layout: "bottomRight",
        timeout: 1000
    }).show();
}

function failNoty(jqXHR) {
    closeNoty();
    let errorInfo = jqXHR.responseJSON;
    failedNote = new Noty({
        text: "<span class='fa fa-lg fa-exclamation-circle'></span> &nbsp;" + i18n["common.errorStatus"] + ": " + errorInfo.status +
            "<br>" + errorInfo.message + (errorInfo.details != null ? "<br>" + errorInfo.details.join("<br>") : ""),
        type: "error",
        layout: "bottomRight"
    });
    failedNote.show();
}