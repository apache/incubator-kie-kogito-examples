let connected = false;

function refreshLoans() {
    showSpinnerDialog("Loading loans");
    $.getJSON("{workflowURL}/loanbroker", (subs) => {
        closeSpinnerDialog();
        printLoansTable(subs);
    }).fail(function () {
        closeSpinnerDialog();
        showError("An error was produced during the loans refresh, please check that que loanbroker-flow application is running.");
    });
}

function printLoansTable(loans) {
    const loansTable = $('#pendingLoans');
    loansTable.children().remove();
    const loansTableBody = $('<tbody>').appendTo(loansTable);
    printLoansTableHeader(loansTable)
    for (const loan of loans) {
        printLoanRow(loansTableBody, loan)
    }
}

function printLoansTableHeader(subscriptionTable) {
    const header = $('<thead class="thead-dark">').appendTo(subscriptionTable);
    const headerTr = $('<tr>').appendTo(header);
    $('<th scope="col">#Workflow instance</th>').appendTo(headerTr);
    $('<th scope="col">SSN</th>').appendTo(headerTr);
    $('<th scope="col">Term</th>').appendTo(headerTr);
    $('<th scope="col">Amount</th>').appendTo(headerTr);
    $('<th scope="col">Score</th>').appendTo(headerTr);
    $('<th scope="col">History</th>').appendTo(headerTr);
}

function printLoanRow(loanTableBody, loan) {
    if (loan.workflowdata && loan.workflowdata.credit) {
        const queryRow = $('<tr>').appendTo(loanTableBody);
        queryRow.append($(`<th scope="row" style="width: 30%;">$\{loan.id}</th>`));
        queryRow.append($(`<td style="width: 30%;">$\{loan.workflowdata.credit.SSN}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{loan.workflowdata.term}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{loan.workflowdata.amount}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{loan.workflowdata.credit.score}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{loan.workflowdata.credit.history}</td>`));
    }
}

function printQuoteRow(quotesResponse) {
    const quoteTableBody = $('#completedQuotes > tbody');
    if (quotesResponse.loanRequestId) {
        const queryRow = $('<tr>').appendTo(quoteTableBody);
        queryRow.append($(`<th scope="row" style="width: 30%;">$\{quotesResponse.loanRequestId}</th>`));
        queryRow.append($(`<td style="width: 30%;">$\{quotesResponse.credit.SSN}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{quotesResponse.term}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{quotesResponse.amount}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{quotesResponse.credit.score}</td>`));
        queryRow.append($(`<td style="width: 30%;">$\{quotesResponse.credit.history}</td>`));

        if (quotesResponse.eventType === "kogito.serverless.workflow.aggregated.quotes.timeout") {
            showEventsToast("No bank offered for Workflow Id: " + quotesResponse.loanRequestId + " and SSN: " + quotesResponse.credit.SSN + " :(");
            const quotesRowBody = $('<tr>').append('<td colspan="6" id="">')
            quotesRowBody.children(0).append('<span>No bank offers were received in the given time frame, maybe you can try with a different amount.</span>');
            quotesRowBody.appendTo(quoteTableBody);
        } else {
            if (quotesResponse.quotes) {
                showEventsToast(quotesResponse.quotes.length + " bank offers arrived for Workflow Id: " + quotesResponse.loanRequestId + " and SSN: " + quotesResponse.credit.SSN + "!");
                const quotesRowBody = $('<tr>').append('<td colspan="6" id="">')
                quotesRowBody.children(0).append('<table class="table mb-0">');
                quotesRowBody.children(0).children(0).append('<thead><tr><th>Bank</th><th>Rate</th></tr></thead>');
                quotesRowBody.children(0).children(0).append('<tbody id="quoteBody">');
                for (const q of quotesResponse.quotes) {
                    const quotesRow = $('<tr>');
                    quotesRow.append($(`<td>$\{q.bankId}</td>`));
                    quotesRow.append($(`<td>$\{q.rate}</td>`));
                    quotesRowBody.find('#quoteBody').append(quotesRow);
                }
                quotesRowBody.appendTo(quoteTableBody);
            }
        }
    }
}

function showError(message) {
    const notification = $(`<div class="toast" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 30rem"/>`)
        .append($(`<div class="toast-header bg-danger">
                 <strong class="me-auto text-white">Error</strong>
                 <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
               </div>`))
        .append($(`<div class="toast-body"/>`)
            .append($(`<p/>`).text(message))
        );
    $("#notificationPanel").append(notification);
    notification.toast({
        delay: 30000
    });
    notification.toast("show");
}

function showLoanForm() {
    const form = $('#newLoanForm');
    form.find('#txtSSN').val('');
    form.find('#txtTerm').val('');
    form.find('#txtAmount').val('');
    form.modal('show');
}

function newLoanRequest() {
    const form = $('#newLoanForm');
    const ssn = form.find('#txtSSN').val();
    const term = form.find('#txtTerm').val();
    const amount = form.find('#txtAmount').val();
    const processInputJson = {
        "workflowdata": {
            "SSN": ssn,
            "term": parseInt(term),
            "amount": parseInt(amount)
        }
    };
    const processInput = JSON.stringify(processInputJson);
    showSpinnerDialog("Creating loan request");
    $.ajax({
        url: "{workflowURL}/loanbroker",
        type: "POST",
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        data: processInput,
        success: function (result) {
            console.log(JSON.stringify(result));
            form.modal('hide');
            closeSpinnerDialog();
            refreshLoans();
        },
        error: function (xhr, status, error) {
            form.modal('hide');
            closeSpinnerDialog();
            showError("An error was produced during the serverless workflow instance create attempt, please check that que loanbroker-flow application is running:\n" + xhr.responseText);
        }
    });
}

function connectNewQuoteSocket() {
    if (connected) {
        return;
    }

    const socket = new WebSocket("ws://" + location.host + "/socket/quote/new");
    socket.onopen = function () {
        connected = true;
        console.log("Connected to backend");
    };
    socket.onmessage = function (m) {
        console.log("New quote: " + m.data);
        const quotesResponse = JSON.parse(m.data);
        printQuoteRow(quotesResponse);
    }
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

function showEventsToast(message) {
    const eventsToast = document.getElementById("eventsToast");
    const toastBody = eventsToast.getElementsByClassName("toast-body")[0];
    const txt = toastBody.children[0].textContent = message;
    const toast = new bootstrap.Toast(eventsToast);
    toast.show();
}

$(document).ready(function () {
    //Initialize button listeners
    $('#refreshButton').click(function () {
        refreshLoans();
    });

    $("#askNewLoan").click(function () {
        showLoanForm();
    });

    //Initial queries loading
    refreshLoans();
    // listen to new quotes as we receive them
    connectNewQuoteSocket();
});