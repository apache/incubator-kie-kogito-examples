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
let PENDING_QUERIES = new Map();

function refreshPendingQueriesTable() {
    $.getJSON("/query-service", (pendingQueries) => {
        printPendingQueriesTable(pendingQueries);
    })
            .fail(function () {
                showError("An error was produced during the pending queries refresh, please check that the query-service application is running.");
            });
}

function printPendingQueriesTable(pendingQueries) {
    PENDING_QUERIES = new Map();
    const pendingQueriesTable = $('#pendingQueriesTable');
    pendingQueriesTable.children().remove();
    const pendingQueriesTableBody = $('<tbody>').appendTo(pendingQueriesTable);
    printPendingQueriesTableHeader(pendingQueriesTable)
    for (const pendingQuery of pendingQueries) {
        PENDING_QUERIES.set(pendingQuery.processInstanceId, pendingQuery);
        printPendingQueryRow(pendingQueriesTableBody, pendingQuery)
    }
}

function printPendingQueriesTableHeader(pendingQueriesTable) {
    const header = $('<thead class="thead-dark">').appendTo(pendingQueriesTable);
    const headerTr = $('<tr>').appendTo(header);
    $('<th scope="col">#Process instance</th>').appendTo(headerTr);
    $('<th scope="col">Pending Query</th>').appendTo(headerTr);
    $('<th scope="col">Resolve</th>').appendTo(headerTr);
}

function printPendingQueryRow(pendingQueriesTableBody, pendingQuery) {
    const pendingQueryRow = $('<tr>').appendTo(pendingQueriesTableBody);
    pendingQueryRow.append($(`<th scope="row" style="width: 30%;">${pendingQuery.processInstanceId}</th>`));
    pendingQueryRow.append($(`<td style="width: 60%;">${pendingQuery.query}</td>`));
    pendingQueryRow.append($(`<td style="width: 10%;"><button type="button" class="btn btn-primary" onclick="showResolveQueryForm('${pendingQuery.processInstanceId}')">Resolve</button></td>`));
}

function showResolveQueryForm(processInstanceId) {
    const pendingQuery = PENDING_QUERIES.get(processInstanceId);
    const form = $('#resolveQueryForm');
    form.find('#queryId').text(processInstanceId);
    form.find('#processInstanceId').val(processInstanceId);
    form.find('#query').val(pendingQuery.query);
    form.find('#response').val('');
    form.modal('show');
}

function resolveQuery() {
    const form = $('#resolveQueryForm');
    const processInstanceId = form.find('#processInstanceId').val();
    const response = form.find('#response').val();
    const resolveRequestJson = {
        "processInstanceId": processInstanceId,
        "queryResponse": response
    }
    const resolveRequest = JSON.stringify(resolveRequestJson);
    $.ajax({
        url: "/query-service/resolveQuery",
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        data: resolveRequest,
        success: function () {
            form.modal('hide');
            refreshPendingQueriesTable();
        },
    }).fail(function () {
        form.modal('hide');
        showError("An error was produced during the query response sending, please check that the query-service application is running.");
    });
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
    refreshPendingQueriesTable();

    $('#refreshButton').click(function () {
        refreshPendingQueriesTable();
    });
});