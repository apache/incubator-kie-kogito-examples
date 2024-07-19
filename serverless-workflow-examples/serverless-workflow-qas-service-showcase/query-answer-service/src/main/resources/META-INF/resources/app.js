function refreshQueriesTable() {
    $.getJSON("/queries", (queries) => {
        printQueriesTable(queries);
    })
            .fail(function () {
                showError("An error was produced during the queries refresh, please check that que query-answer-service application is running.");
            });
}

function printQueriesTable(queries) {
    const queriesTable = $('#queriesTable');
    queriesTable.children().remove();
    const queriesTableBody = $('<tbody>').appendTo(queriesTable);
    printQueriesTableHeader(queriesTable)
    for (const query of queries) {
        printQueryRow(queriesTableBody, query)
    }
}

function printQueriesTableHeader(queriesTable) {
    const header = $('<thead class="thead-dark">').appendTo(queriesTable);
    const headerTr = $('<tr>').appendTo(header);
    $('<th scope="col">#Process instance</th>').appendTo(headerTr);
    $('<th scope="col">Query Status</th>').appendTo(headerTr);
    $('<th scope="col">Query</th>').appendTo(headerTr);
    $('<th scope="col">Answer</th>').appendTo(headerTr);
}

function printQueryRow(queriesTableBody, query) {
    const queryRow = $('<tr>').appendTo(queriesTableBody);
    queryRow.append($(`<th scope="row" style="width: 30%;">${query.processInstanceId}</th>`));
    queryRow.append($(`<td style="width: 10%;">${query.status}</td>`));
    queryRow.append($(`<td style="width: 30%;">${query.query}</td>`));
    queryRow.append($(`<td style="width: 30%;">${query.answer}</td>`));
}

function showCreateQueryForm() {
    const form = $('#createQueryForm');
    form.find('#query').val('');
    form.modal('show');
}

function createQuery() {
    const form = $('#createQueryForm');
    const query = form.find('#query').val();
    const processInputJson = {
        "query": query
    };
    const processInput = JSON.stringify(processInputJson);
    $.ajax({
        url: "/qaservice",
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        data: processInput,
        success: function (result) {
            console.log(JSON.stringify(result));
            form.modal('hide');
            refreshQueriesTable();
        },
    }).fail(function () {
        form.modal('hide');
        showError("An error was produced during the serverless workflow instance create attempt, please check that que query-answer-service application is running.");
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
    refreshQueriesTable();

    //Initialize button listeners
    $('#refreshButton').click(function () {
        refreshQueriesTable();
    });

    $("#createQueryButton").click(function () {
        showCreateQueryForm();
    });

});