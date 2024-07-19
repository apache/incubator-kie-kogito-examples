function refreshTables() {
    refreshSwitchStateTimeoutsTable();
    refreshCallbackStateTimeoutsTable();
    refreshEventStateTimeoutsTable();
    refreshWorkflowTimeoutsTable();
}

function refreshSwitchStateTimeoutsTable() {
    $.getJSON("/switch_state_timeouts", (instances) => {
        console.log(instances);
        printSwitchStateTimeoutsTable(instances);
    })
            .fail(function () {
                showError("An error was produced during the switch_state_timeouts table refresh, please check that server is running.");
            });
}

function refreshCallbackStateTimeoutsTable() {
    $.getJSON("/callback_state_timeouts", (instances) => {
        console.log(instances);
        printCallbackStateTimeoutsTable(instances);
    })
            .fail(function () {
                showError("An error was produced during the callback_state_timeouts table refresh, please check that server is running.");
            });
}

function refreshEventStateTimeoutsTable() {
    $.getJSON("/event_state_timeouts", (instances) => {
        console.log(instances);
        printEventStateTimeoutsTable(instances);
    })
            .fail(function () {
                showError("An error was produced during the event_state_timeouts table refresh, please check that server is running.");
            });
}

function refreshWorkflowTimeoutsTable() {
    $.getJSON("/workflow_timeouts", (instances) => {
        console.log(instances);
        printWorkflowTimeoutsTable(instances);
    })
        .fail(function () {
            showError("An error was produced during the workflow_timeouts table refresh, please check that server is running.");
        });
}

function printSwitchStateTimeoutsTable(instances) {
    const table = $('#switchStateTimeoutsTable');
    table.children().remove();
    const tableBody = $('<tbody>').appendTo(table);
    printSwitchStateTimeoutsTableHeader(table)
    for (const instance of instances) {
        printSwitchStateTimeoutsRow(tableBody, instance)
    }
}

function printSwitchStateTimeoutsTableHeader(table) {
    const header = $('<thead class="thead-dark">').appendTo(table);
    const headerTr = $('<tr class="d-flex">').appendTo(header);
    $('<th scope="col" class="col-4">#Workflow instance</th>').appendTo(headerTr);
    $('<th scope="col" class="col-2"></th>').appendTo(headerTr);
    $('<th scope="col" class="col-2"></th>').appendTo(headerTr);
}

function printSwitchStateTimeoutsRow(tableBody, instance) {
    const tableRow = $('<tr class="d-flex">').appendTo(tableBody);
    tableRow.append($(`<th scope="row" class="col-4">${instance.id}</th>`));

    const approveBtn = $(`<button id="approveVisaBtn_${instance.id}" type="button" class="btn btn-primary btn-sm">Approve visa</button>`);
    const approveButtonTd = $(`<td class="col-2"></td>`);
    approveButtonTd.append(approveBtn);
    tableRow.append(approveButtonTd);
    approveBtn.click(function () {
        sendVisaApprovalEvent(instance.id);
    });

    const denyBtn = $(`<button id="denyVisaBtn_${instance.id}" type="button" class="btn btn-danger btn-sm">Deny visa</button>`);
    const denyButtonTd = $(`<td class="col-2"></td>`);
    denyButtonTd.append(denyBtn);
    tableRow.append(denyButtonTd);
    denyBtn.click(function () {
        sendVisaDenyEvent(instance.id);
    });
}

function printCallbackStateTimeoutsTable(instances) {
    const table = $('#callbackStateTimeoutsTable');
    table.children().remove();
    const tableBody = $('<tbody>').appendTo(table);
    printCallbackStateTimeoutsTableHeader(table)
    for (const instance of instances) {
        printCallbackStateTimeoutsRow(tableBody, instance);
    }
}

function printCallbackStateTimeoutsTableHeader(table) {
    const header = $('<thead class="thead-dark">').appendTo(table);
    const headerTr = $('<tr class="d-flex">').appendTo(header);
    $('<th scope="col" class="col-4">#Workflow instance</th>').appendTo(headerTr);
    $('<th scope="col" class="col-2"></th>').appendTo(headerTr);
}

function printCallbackStateTimeoutsRow(tableBody, instance) {
    const tableRow = $('<tr class="d-flex">').appendTo(tableBody);
    tableRow.append($(`<th scope="row" class="col-4">${instance.id}</th>`));

    const callbackEventBtn = $(`<button id="callbackEventBtn_${instance.id}" type="button" class="btn btn-primary btn-sm">Send callback event</button>`);
    const callbackEventBtnTd = $(`<td class="col-2"></td>`);
    callbackEventBtnTd.append(callbackEventBtn);
    tableRow.append(callbackEventBtnTd);
    callbackEventBtn.click(function () {
        sendCallbackEvent(instance.id);
    });
}

function printEventStateTimeoutsTable(instances) {
    const table = $('#eventStateTimeoutsTable');
    table.children().remove();
    const tableBody = $('<tbody>').appendTo(table);
    printEventStateTimeoutsTableHeader(table)
    for (const instance of instances) {
        printEventStateTimeoutsRow(tableBody, instance)
    }
}

function printEventStateTimeoutsTableHeader(table) {
    const header = $('<thead class="thead-dark">').appendTo(table);
    const headerTr = $('<tr class="d-flex">').appendTo(header);
    $('<th scope="col" class="col-4">#Workflow instance</th>').appendTo(headerTr);
    $('<th scope="col" class="col-2"></th>').appendTo(headerTr);
    $('<th scope="col" class="col-2"></th>').appendTo(headerTr);
}

function printEventStateTimeoutsRow(tableBody, instance) {
    const tableRow = $('<tr class="d-flex">').appendTo(tableBody);
    tableRow.append($(`<th scope="row" class="col-4">${instance.id}</th>`));

    const sendEvent1Btn = $(`<button id="sendEvent1Btn_${instance.id}" type="button" class="btn btn-primary btn-sm">Send event1</button>`);
    const sendEvent1ButtonTd = $(`<td class="col-2"></td>`);
    sendEvent1ButtonTd.append(sendEvent1Btn);
    tableRow.append(sendEvent1ButtonTd);
    sendEvent1Btn.click(function () {
        sendEvent1(instance.id);
    });

    const sendEvent2Btn = $(`<button id="sendEvent2Btn_${instance.id}" type="button" class="btn btn-primary btn-sm">Send event2</button>`);
    const sendEvent2ButtonTd = $(`<td class="col-2"></td>`);
    sendEvent2ButtonTd.append(sendEvent2Btn);
    tableRow.append(sendEvent2ButtonTd);
    sendEvent2Btn.click(function () {
        sendEvent2(instance.id);
    });
}

function printWorkflowTimeoutsTable(instances) {
    const table = $('#workflowTimeoutsTable');
    table.children().remove();
    const tableBody = $('<tbody>').appendTo(table);
    printWorkflowTimeoutsTableHeader(table)
    for (const instance of instances) {
        printWorkflowTimeoutsRow(tableBody, instance);
    }
}

function printWorkflowTimeoutsTableHeader(table) {
    const header = $('<thead class="thead-dark">').appendTo(table);
    const headerTr = $('<tr class="d-flex">').appendTo(header);
    $('<th scope="col" class="col-4">#Workflow instance</th>').appendTo(headerTr);
    $('<th scope="col" class="col-2"></th>').appendTo(headerTr);
}

function printWorkflowTimeoutsRow(tableBody, instance) {
    const tableRow = $('<tr class="d-flex">').appendTo(tableBody);
    tableRow.append($(`<th scope="row" class="col-4">${instance.id}</th>`));

    const wakeUpEventBtn = $(`<button id="wakeUpEventBtn_${instance.id}" type="button" class="btn btn-primary btn-sm">Send wake up event</button>`);
    const wakeUpEventBtnTd = $(`<td class="col-2"></td>`);
    wakeUpEventBtnTd.append(wakeUpEventBtn);
    tableRow.append(wakeUpEventBtnTd);
    wakeUpEventBtnTd.click(function () {
        sendWakeUpEvent(instance.id);
    });
}

function sendVisaApprovalEvent(processInstanceId) {
    produceEvent("/events-producer/produce-switch-state-timeouts-visa-approved-event", processInstanceId, "Approved from UI", function () {
        disableSwitchStateTimeoutsButtons(processInstanceId);
        showEventsToast();
    })
}

function sendVisaDenyEvent(processInstanceId) {
    produceEvent("/events-producer/produce-switch-state-timeouts-visa-denied-event", processInstanceId, "Denied from from UI", function () {
        disableSwitchStateTimeoutsButtons(processInstanceId);
        showEventsToast();
    })
}

function sendCallbackEvent(processInstanceId) {
    produceEvent("/events-producer/produce-callback-state-timeouts-event", processInstanceId, "Callback event sent from UI", function () {
        disableCallbackStateTimeoutsButtons(processInstanceId);
        showEventsToast();
    })
}

function sendEvent1(processInstanceId) {
    produceEvent("/events-producer/produce-event-state-timeouts-event1", processInstanceId, "Event1 sent from UI", function () {
        disableEventStateTimeoutsButtons(processInstanceId);
        showEventsToast();
    })
}

function sendEvent2(processInstanceId) {
    produceEvent("/events-producer/produce-event-state-timeouts-event2", processInstanceId, "Event2 sent from UI", function () {
        disableEventStateTimeoutsButtons(processInstanceId);
        showEventsToast();
    })
}

function sendWakeUpEvent(processInstanceId) {
    produceEvent("/events-producer/produce-workflow-timeouts-event", processInstanceId, "Wake up event sent from UI", function () {
        disableWorkflowTimeoutsButtons(processInstanceId);
        showEventsToast();
    })
}

function startNewSwitchStateTimeouts() {
    startProcess("/switch_state_timeouts", function () {
        refreshSwitchStateTimeoutsTable();
    });
}

function startNewCallbackStateTimeouts() {
    startProcess("/callback_state_timeouts", function () {
        refreshCallbackStateTimeoutsTable();
    });
}

function startNewEventStateTimeouts() {
    startProcess("/event_state_timeouts", function () {
        refreshEventStateTimeoutsTable();
    });
}

function startNewWorkflowTimeouts() {
    startProcess("/workflow_timeouts", function () {
        refreshWorkflowTimeoutsTable();
    });
}

function startProcess(endpoint, onSuccess) {
    const processInputJson = { };
    const processInput = JSON.stringify(processInputJson);
    $.ajax({
        url: endpoint,
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        data: processInput,
        success: function (result) {
            onSuccess.call();
        },
    }).fail(function () {
        showError("An error was produced when creating a new serverless workflow instance: " + endpoint + ", please check that server is running.");
    });
}

function produceEvent(endpoint, processInstanceId, eventData, onSuccess) {
    $.ajax({
        url: endpoint + "/" + processInstanceId,
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        data: "{ \"eventData\": \"" + eventData + "\" }",
        success: function () {
            onSuccess.call();
        },
    }).fail(function () {
        showError("An error was produced while sending an event to server endpoint: " + endpoint + "/" + processInstanceId + ", please check that server is running");
    });
}

function disableSwitchStateTimeoutsButtons(processInstanceId) {
    $(`#approveVisaBtn_${processInstanceId}`).prop('disabled', true);
    $(`#denyVisaBtn_${processInstanceId}`).prop('disabled', true);
}

function disableCallbackStateTimeoutsButtons(processInstanceId) {
    $(`#callbackEventBtn_${processInstanceId}`).prop('disabled', true);
}

function disableEventStateTimeoutsButtons(processInstanceId) {
    $(`#sendEvent1Btn_${processInstanceId}`).prop('disabled', true);
    $(`#sendEvent2Btn_${processInstanceId}`).prop('disabled', true);
}

function disableWorkflowTimeoutsButtons(processInstanceId) {
    $(`#wakeUpEventBtn_${processInstanceId}`).prop('disabled', true);
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

function showEventsToast() {
    const eventsToast = document.getElementById("eventsToast");
    const toast = new bootstrap.Toast(eventsToast);
    toast.show();
}

$(document).ready(function () {
    //Initial queries loading
    refreshTables();

    //Initialize button listeners
    $('#refreshSwitchStateTimeoutsButton').click(function () {
        refreshSwitchStateTimeoutsTable();
    });

    $('#refreshCallbackStateTimeoutsButton').click(function () {
        refreshCallbackStateTimeoutsTable();
    });

    $('#refreshEventStateTimeoutsButton').click(function () {
        refreshEventStateTimeoutsTable();
    });

    $('#refreshWorkflowTimeoutsButton').click(function () {
        refreshWorkflowTimeoutsTable();
    });

    $("#startSwitchStateTimeoutsButton").click(function () {
        startNewSwitchStateTimeouts();
    });

    $("#startCallbackStateTimeoutsButton").click(function () {
        startNewCallbackStateTimeouts();
    });

    $("#startEventStateTimeoutsButton").click(function () {
        startNewEventStateTimeouts();
    });

    $("#startWorkflowTimeoutsButton").click(function () {
        startNewWorkflowTimeouts();
    });

});