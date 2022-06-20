function refreshSubscriptionTable() {
    $.getJSON("/subscription/pending", (subscriptions) => {
        printPendingSubsTable(subscriptions);
    }).fail(function () {
        showError("An error was produced during the Subscriptions refresh, please check that que subscription-service application is running.");
    });

    $.getJSON("/subscription/verified", (subscriptions) => {
        printVerifiedSubsTable(subscriptions);
    }).fail(function () {
        showError("An error was produced during the Subscriptions refresh, please check that que subscription-service application is running.");
    });
}

function printPendingSubsTable(subscriptions) {
    printTable(subscriptions, '#pendingSubscriptions');
}

function printVerifiedSubsTable(subscriptions) {
    printTable(subscriptions, '#verifiedSubscriptions');
}

function printTable(subscriptions, tableId) {
    const pendingSubscriptions = $(tableId);
    pendingSubscriptions.children().remove();
    const queriesTableBody = $('<tbody>').appendTo(pendingSubscriptions);
    printSubscriptionsTableHeader(pendingSubscriptions)
    for (const subscription of subscriptions) {
        printSubscriptionRow(queriesTableBody, subscription)
    }
}

function printSubscriptionsTableHeader(subsTable) {
    const header = $('<thead class="thead-dark">').appendTo(subsTable);
    const headerTr = $('<tr>').appendTo(header);
    $('<th scope="col">#Workflow instance</th>').appendTo(headerTr);
    $('<th scope="col">Email</th>').appendTo(headerTr);
    $('<th scope="col">Name</th>').appendTo(headerTr);
}

function printSubscriptionRow(subsTableBody, subscription) {
    const queryRow = $('<tr>').appendTo(subsTableBody);
    queryRow.append($(`<th scope="row" style="width: 30%;">${subscription.id}</th>`));
    queryRow.append($(`<td style="width: 10%;">${subscription.email}</td>`));
    queryRow.append($(`<td style="width: 30%;">${subscription.name}</td>`));
}

function showError(message) {
    const notification = $(`<div class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 30rem"/>`)
            .append($(`<div class="toast-header bg-danger">
                 <strong class="mr-auto text-dark">Error</strong>
                 <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                   <span aria-hidden="true">&times;</span>
                 </button>
               </div>`))
            .append($(`<div class="toast-body"/>`)
                    .append($(`<p/>`).text(message))
            );
    $("#notificationPanel").append(notification);
    notification.toast({delay: 30000});
    notification.toast("show");
}

$(document).ready(function () {
    //Initial queries loading
    refreshSubscriptionTable();

    //Initialize button listeners
    $('#refreshButton').click(function () {
        refreshSubscriptionTable();
    });
});