/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
function refreshSubscriptionTable() {
    showSpinnerDialog("Loading subscriptions");
    $.getJSON("/subscription/pending", (subscriptions) => {
        closeSpinnerDialog();
        printPendingSubsTable(subscriptions);
    }).fail(function () {
        closeSpinnerDialog();
        showError("An error was produced during the Pending subscriptions refresh, please check that que subscription-service application is running.");
    });

    $.getJSON("/subscription/verified", (subscriptions) => {
        closeSpinnerDialog();
        printVerifiedSubsTable(subscriptions);
    }).fail(function () {
        closeSpinnerDialog();
        showError("An error was produced during the Verified subscriptions refresh, please check that que subscription-service application is running.");
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
    refreshSubscriptionTable();

    //Initialize button listeners
    $('#refreshButton').click(function () {
        refreshSubscriptionTable();
    });
});