function refreshSubsTable() {
    showSpinnerDialog("Loading subscriptions");
    $.getJSON("/subscription_flow", (subs) => {
        closeSpinnerDialog();
        printSubsTable(subs);
    }).fail(function () {
        closeSpinnerDialog();
        showError("An error was produced during the subscriptions refresh, please check that que subscription-flow application is running.");
    });
}

function printSubsTable(subs) {
    const subsTable = $('#pendingSubscriptions');
    subsTable.children().remove();
    const subsTableBody = $('<tbody>').appendTo(subsTable);
    printSubsTableHeader(subsTable)
    for (const sub of subs) {
        printSubsRow(subsTableBody, sub.workflowdata)
    }
}

function printSubsTableHeader(subscriptionTable) {
    const header = $('<thead class="thead-dark">').appendTo(subscriptionTable);
    const headerTr = $('<tr>').appendTo(header);
    $('<th scope="col">#Workflow instance</th>').appendTo(headerTr);
    $('<th scope="col">Email</th>').appendTo(headerTr);
    $('<th scope="col">Name</th>').appendTo(headerTr);
    $('<th scope="col">Confirm</th>').appendTo(headerTr);
}

function printSubsRow(subscriptionTableBody, subscription) {
    const queryRow = $('<tr>').appendTo(subscriptionTableBody);
    queryRow.append($(`<th scope="row" style="width: 30%;">${subscription.id}</th>`));
    queryRow.append($(`<td style="width: 30%;">${subscription.email}</td>`));
    queryRow.append($(`<td style="width: 30%;">${subscription.name}</td>`));
    queryRow.append($(`<td style="width: 30%;"><span class="input-group-btn"><button class="btn btn-default btn-sm" onclick="confirmSubscription('${subscription.id}', '${subscription.email}')"><i class="bi bi-check-square-fill"></i></button></span></td>`))
}

function showError(message) {
    const notification = $(`<div class="toast" role="alert" aria-live="assertive" aria-atomic="true"  data-bs-delay="3000"/>`)
            .append($(`<div class="toast-header bg-danger">
                 <strong class="me-auto text-dark">Error</strong>
                 <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
               </div>`))
            .append($(`<div class="toast-body"/>`)
                    .append($(`<p/>`).text(message))
            );
    $("#notificationPanel").append(notification);
    notification.toast("show");
}

function showNewSubForm() {
    const form = $('#newSubscriptionForm');
    form.find('#txtName').val('');
    form.find('#txtEmail').val('');
    form.modal('show');
}

function newSubscription() {
    const form = $('#newSubscriptionForm');
    const name = form.find('#txtName').val();
    const email = form.find('#txtEmail').val();
    const processInputJson = {
        "workflowdata": {
            "email": email,
            "name": name
        }
    };
    const processInput = JSON.stringify(processInputJson);
    form.modal('hide');
    showSpinnerDialog("Creating subscription");
    $.ajax({
        url: "/subscription_flow",
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        data: processInput,
        success: function (result) {
            console.log(JSON.stringify(result));
            closeSpinnerDialog();
            refreshSubsTable();
        },
    }).fail(function () {
        closeSpinnerDialog()
        showError("An error was produced during the serverless workflow instance create attempt, please check that que subscription-flow application is running.");
    });
}

// from: https://stackoverflow.com/questions/105034/how-to-create-a-guid-uuid
function createUUID() {
    // http://www.ietf.org/rfc/rfc4122.txt
    const s = [];
    const hexDigits = "0123456789abcdef";
    for (let i = 0; i < 36; i++) {
        s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
    s[8] = s[13] = s[18] = s[23] = "-";

    return s.join("");
}

function confirmSubscription(subscriptionId, subscriptionEmail) {
    const cloudEventJson = {
        specversion: "1.0",
        id: createUUID(),
        source: "webApp",
        type: "confirm.subscription",
        kogitoprocrefid: subscriptionId,
        datacontenttype: "application/json",
        data: {
            id: subscriptionId,
            confirmed: true
        }
    }
    const ceInput = JSON.stringify(cloudEventJson);
    showSpinnerDialog("Confirming subscription: " + subscriptionId + ", " + subscriptionEmail);
    $.ajax({
        url: "/",
        type: "POST",
        dataType: "text",
        contentType: "application/cloudevents+json; charset=UTF-8",
        data: ceInput,
        success: function (result) {
            console.log(result);
            closeSpinnerDialog();
            // TODO: ideally, we have a websocket listening for the new subscription event, then we update the table
            setTimeout(refreshSubsTable, 2000);
        },
    }).fail(function (xhr, status, error) {
        closeSpinnerDialog();
        console.log(error);
        showError("An error '" + xhr.responseText + "' (status: " + status + ") was produced during the serverless workflow instance create attempt, please check that que subscription-flow application is running.");
    });
}

function showSpinnerDialog(message) {
    const modal = $('#spinnerDialog');
    modal.find('#spinnerDialogMessage').text(message);
    modal.show();
}

function closeSpinnerDialog() {
    const modal = $('#spinnerDialog');
    modal.hide();
}

$(document).ready(function () {
    //Initial queries loading
    refreshSubsTable();

    //Initialize button listeners
    $('#refreshButton').click(function () {
        refreshSubsTable();
    });

    $("#newSubscription").click(function () {
        showNewSubForm();
    });

});