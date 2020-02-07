/**
 * Renders the display of a flight
 * @param {object} flight - The flight to display
 * 
 * @returns jQuery
 */
function renderFlight(flight, tasks) {
    let header;
    const finalizePassengerListTasks = findTasks("finalizePassengerList", tasks);
    if (finalizePassengerListTasks.length === 1) {
        const generatePassengerElement = (flight.flight.passengerList.length === 0)? [
            element("button", { class: "btn btn-secondary", onClick: () => generatePassengersForFlight(flight) }, "Generate Passenger List")
        ]: []
        header = element("div", {},
            element("h5", {}, getFlightName(flight)),
            element("button", {
                class: "btn btn-secondary",
                role: "button",
                "data-flight": flight.id,
                "data-action": "add",
                "data-toggle": "modal",
                "data-target": "#add-passenger-modal",
            },
            "Add Passenger"
            ),
            ...generatePassengerElement,
            element("button", {
                class: "btn btn-primary",
                role: "button",
                onClick: () => {
                    $.post(`/rest/flights/${flight.id}/finalizePassengerList/${finalizePassengerListTasks[0]}`, JSON.stringify({}), () => {
                        refresh();
                    });
                }
            }, "Finalize Passenger List")
        );
    }
    else {
        const finalizeSeatAssignmentTasks = findTasks("finalizeSeatAssignment", tasks);
        const finalizeSeatAssignmentButton = (finalizeSeatAssignmentTasks.length === 1)? [element("button", {
            class: "btn btn-primary",
            role: "button",
            onClick: () => {
                $.post(`/rest/flights/${flight.id}/finalizeSeatAssignment/${finalizeSeatAssignmentTasks[0]}`, JSON.stringify({}), () => {
                    refresh();
                });
            }
        }, "Finalize Seat Assignments")] : [];

        header = element("div", {},
            element("h5", {}, getFlightName(flight)),
            ...finalizeSeatAssignmentButton
        );
    }

    const getSeatName = (seat) => {
        const seatRegex = /(\d+);(\d+)/;
        const matches = seat.match(seatRegex);
        const row = parseInt(matches[1]);
        const column = parseInt(matches[2]);
        return flight.flight.seatList.find(s => s.row === row && s.column === column).name;
    };
    const passengerRequestString = passenger => `${passenger.name}, Is Paying for Seat? ${passenger.payedForSeat? "Yes, Seat " + getSeatName(passenger.seat) : "No"}`;
    return element("div", {},
      element("div", {},
            header,
            element("div", { style: "display: grid; grid-template-columns: 1fr 1fr;"},
                element("div", { style: "grid-column: 1;" },
                    // Passenger Request List
                    suspense(element("div", {}, "is loading"),
                    element("div", { class: "list-group" }, ...findTasks("approveDenyPassenger", tasks).map(futureTask => future(futureTask, { isLoading: true },
                        resolve => $.getJSON(`/rest/flights/${flight.id}/approveDenyPassenger/${futureTask}`, resolve),
                        task => element(
                        "div", { class: "list-group-item list-group-item-action flex-column align-items-start d-flex w-100 justify-content-between" },
                        // Note: we take advantage that undefined is falsey here (task doesn't have property "isLoading")
                        element("span", {},
                            element("i", { class: "fas fa-user" }, ""),
                            element("span", {}, task.isLoading? "" : passengerRequestString(task.passenger)),
                        ),
                        element("span", {},
                            element("button", task.isLoading? { class: "btn btn-primary" } : {
                                class: "btn btn-primary",
                                onClick: () => {
                                    $.post(`/rest/flights/${flight.id}/approveDenyPassenger/${task.id}`, JSON.stringify({
                                        isPassengerApproved: true
                                    }), () => {
                                        refresh();
                                    });
                                }
                        }, "Approve"),
                        element("button", task.isLoading? { class: "btn btn-danger" } : {
                            class: "btn btn-danger",
                            onClick: () => {
                                $.post(`/rest/flights/${flight.id}/approveDenyPassenger/${task.id}`, JSON.stringify({
                                    isPassengerApproved: false
                                }), () => {
                                    refresh();
                                });
                            }
                        }, "Deny")
                    )
            )))))),
            element("div", {},
                flightSeats(flight, ({ passenger }) => (passenger)? element("div", {}, passenger.name) : element("div", { hidden: true}, "")),
                // Approved Passenger List
                element("div", { class: "card" },
                    element("h5", {}, "Passenger List"),
                    element("button", {
                            class: "collapsed",
                            "data-toggle": "collapse",
                            "data-target":  `#${flight.id}-passenger-list`,
                            "aria-expanded": "false",
                            "aria-controls": `#${flight.id}-passenger-list`,
                            onClick: () => $(`#${flight.id}-passenger-list`).collapse("toggle")
                        },
                        "Show/Hide"
                    ),
                    element("div", { id: `${flight.id}-passenger-list`, class: "collapse list-group" }, ...flight.flight.passengerList.map(passenger =>  element(
                        "div", { class: "list-group-item list-group-item-action flex-column align-items-start d-flex w-100 justify-content-between" },
                        element("span", {},
                            element("i", { class: "fas fa-user" }, ""),
                            element("span", {}, passenger.name),
                        ),
                        element("span", {},
                            `Seat: ${passenger.seat? passenger.seat.name : "Unassigned"}`
                        )
                    ))
                    )
                )
            )
        ),
    ));
}

function flightSeats(flight, flightSeatToElementMap) {
    const rowToGridRow = row => 2*row + 1;
    const columnToGridColumn = col => col + Math.round(col / (flight.flight.seatColumnSize)) + 1;
    const sortedSeatList = [...flight.flight.seatList].sort((a,b) => (a.row - b.row === 0)? a.column - b.column : a.row - b.row);
    
    return element("div", { style: `display: grid;
        grid-column: 2;
        grid-auto-flow: dense;
        grid-template-rows: repeat(${2*flight.flight.seatRowSize}, minmax(50px, 1fr));
        grid-template-columns: repeat(${flight.flight.seatColumnSize + 1}, minmax(50px, 1fr));
        justify-items: center;
        align-items: center;
        width: max-content;
        border: 1px solid;
        ` }, ...sortedSeatList.map(seat => element(
          "span", { class: "fas fa-couch", style: `grid-row: ${rowToGridRow(seat.row)}; grid-column: ${columnToGridColumn(seat.column)}`},
          seat.name)
        ),
        ...sortedSeatList.map(seat => flightSeatToElementMap({flight: flight.flight, seat: seat, passenger: flight.flight.passengerList.find(passenger => passenger.seat !== null &&
            passenger.seat.row === seat.row && passenger.seat.column === seat.column) } ).css({"grid-row": String(rowToGridRow(seat.row) + 1), "grid-column": String(columnToGridColumn(seat.column)) }))
    );
}

let myFlights = [];
let flightOrder = [];

function refresh() {
    $("#flights-container").empty();
    $.getJSON("/rest/flights", flights => {
        const toRemove = [...flightOrder];
        flights.forEach(flight => {
            if (!flightOrder.includes(flight.id)) {
                flightOrder.push(flight.id);
            }
            else {
                toRemove.splice(toRemove.indexOf(flight.id), 1);
            }
        });
        toRemove.forEach(flight => flightOrder.splice(flightOrder.indexOf(flight.id), 1));
        flightOrder.forEach(flightId => element("div", { id: flightId }).appendTo("#flights-container"));
        myFlights = flights;
        flights.forEach(flight => {
            $.getJSON(`/rest/flights/${flight.id}/tasks`, tasks => {
                renderFlight(flight, tasks).appendTo(`#${flight.id}`);
            });
        });
    });
}

function getFlightName(flight) {
    return `${flight.flight.origin} -> ${flight.flight.destination}, departing on ${getMomentDate(flight.flight.departureDateTime).format("LLL")}`;
}

function getMomentDate(javaDate) {
    return moment().year(javaDate.year)
        .dayOfYear(javaDate.dayOfYear)
        .hour(javaDate.hour)
        .minute(javaDate.minute);
}

function findTasks(taskName, tasks) {
    return Object.keys(tasks).filter(key => tasks[key] === taskName);
}

function getPassengersToApproveDeny(flight, tasks, map) {
    findTasks("approveDenyPassenger", tasks).map(task => {
        return $.getJSON(`/rest/flights/${flight.id}/approveDenyPassenger/${task}`, map);
    });
}

function generatePassengersForFlight(flight) {
    for (let i = 0; i < flight.flight.seatList.length * 0.8; i++) {
        const newPassengerRequest = JSON.stringify({
            name: randomName(),
            seatTypePreference: "NONE",
            emergencyExitRowCapable: true,
            payedForSeat: false
        });
        $.post(`/rest/flights/${flight.id}/newPassengerRequest`, newPassengerRequest, () => {}, "json");
    }
    setTimeout(() => {
        $.getJSON(`/rest/flights/${flight.id}/tasks`, tasks => {
            getPassengersToApproveDeny(flight, tasks, task => {
                $.post(`/rest/flights/${flight.id}/approveDenyPassenger/${task.id}`, JSON.stringify({
                    isPassengerApproved: true
                }), () => {}, "json");
            });
            setTimeout(refresh, 100);
        });
    }, 100);
}

function initModal() {
    $("#new-flight-modal").on('show.bs.modal', () => {
        const newFlightAction = $("#new-flight-action");
        newFlightAction.unbind();
        const modal = $(this);

        $('.modal-title').text("Create New Flight");
        $('#origin').val("YYZ");
        $('#destination').val("KRND");
        $("#departureDateTime").val(new Date().toISOString().slice(0, -1));

        $("#seatRowSize").val(4);
        $("#seatColumnSize").val(6);

        newFlightAction.click(() => {
            const origin = $('#origin').val();
            const destination = $('#destination').val();
            const departureDateTime = $('#departureDateTime').val();
            const seatRowSize = $('#seatRowSize').val();
            const seatColumnSize = $("#seatColumnSize").val();

            const newFlightData = JSON.stringify({
                params: {
                    origin,
                    destination,
                    departureDateTime,
                    seatRowSize,
                    seatColumnSize
                }
            });

            $.post("/rest/flights", newFlightData, () => {
                refresh();
            }, "json");

            $('#new-flight-modal').modal('toggle');
        });
    });

    $("#add-passenger-modal").on('show.bs.modal', event => {
        var button = $(event.relatedTarget);
        var action = button.data('action');
        var flight = button.data('flight');
        const flightObject = myFlights.find(f => f.id === flight);
        var addPassengerAction = $("#add-passenger-action");
        addPassengerAction.unbind();
        var modal = $("#add-passenger-modal");
        modal.find('.modal-title').text(`Add Passenger to Flight ${getFlightName(flightObject)}`);
        modal.find('#name').val(randomName());
        modal.find('#seatTypePreference').val("NONE");
        modal.find('#emergencyExitRowCapable').prop('checked', false);
        modal.find('#payedForSeat').prop('checked', false);

        modal.find('#seatPicker').replaceWith(element("div", { id: "seatPicker", class: "collapse" }, flightSeats(flightObject, ({seat, passenger}) => element("input",
            { type: "radio", class: "form-control", name: "flight-seat", value: seat.row + ";" + seat.column, disabled: passenger !== undefined, hidden: passenger !== undefined }))));


        addPassengerAction.click(() => {
            const name = $('#name').val();
            const seatTypePreference = $('#seatTypePreference').val();
            const emergencyExitRowCapable = $('#emergencyExitRowCapable').prop( "checked" );
            const payedForSeat = $('#payedForSeat').prop( "checked" );
            const seat = (payedForSeat)? $('input[name="flight-seat"]:checked').val() : null;

            const newPassengerRequest = JSON.stringify({
                    name,
                    seatTypePreference,
                    emergencyExitRowCapable,
                    payedForSeat,
                    seat
            });

            $.post(`/rest/flights/${flight}/newPassengerRequest`, newPassengerRequest, () => {
                refresh();
            }, "json");

            $('#add-passenger-modal').modal('toggle');
        });
    });
}

/** 
 * Returns a new jQuery object with a given element type, attributes, and text/children elements 
 * @param {string} elementType - The type of the element to create (for instance, div)
 * @param {object} props - props to add to the object (class, id, onClick, etc.)
 * @param {string[]|jQuery[]} childs - The children elements of the object; either a single string or a list
 *                                     of jQuery elements
 * @returns jQuery
*/
function element(elementType, props, ...childs) {
    const out = $(`<${elementType} />`);
    const onRegex = /^on.+/g;
    Object.keys(props).forEach(prop => {
        if (prop.match(onRegex)) {
            if (typeof out[prop.substring(2).toLowerCase()] === "function") {
                out[prop.substring(2).toLowerCase()](props[prop]);
            }
            else {
                const msg = `There is no event handler for ${prop} in JQuery.`;
                console.error(msg);
                throw new Error(msg);
            }
        }
        else {
            out.attr(prop, props[prop]);
        }
    });
    if (childs.length === 1 && typeof childs[0] === 'string' || typeof childs[0] === 'number') {
        out.text(childs[0]);
    }
    else {
        childs.forEach(child => {
            if (child instanceof $) {
                out.append(child);
            }
            else {
                const msg = "Only JQuery elements allowed in elements with more than 1 child.";
                console.error(msg);
                throw new Error(msg);
            }
        });
    }
    return out;
}


const memoizeMap = new Map();
/** 
 * Returns a jQuery object created from consumer with the default value, which will be updated
 * when a future promise resolves.
 * @param {object} defaultValue - Default value to be used by the consumer
 * @param {(function resolve) => void} futureValueSupplier - A promise that returns the value to be used by the consumer
 * @param {(object props) => jQuery} consumer - Creates the jquery object to be used
 * @returns jQuery
*/
function future(key, defaultValue, futureValueSupplier, consumer) {
    if (key !== null && memoizeMap.has(key)) {
        return consumer(memoizeMap.get(key));
    }
    const out = consumer(defaultValue);
    out.attr("data-is-loading", true);
    futureValueSupplier(futureValue => {
        if (key !== null) {
            memoizeMap.set(key, futureValue);
        }
        const oldIsLoading = out.parent().attr("data-is-loading");
        if (oldIsLoading !== undefined) {
            const newIsLoading = parseInt(oldIsLoading) - 1;
            if (newIsLoading !== 0) {
                out.parent().attr("data-is-loading", newIsLoading);
            }
            else {
                out.parent().removeAttr("data-is-loading");
            }
        }
        out.replaceWith(consumer(futureValue));
    });
    return out;
}

function suspense(loadingElement, parentElement) {
    const loadingCount = parentElement.children("[data-is-loading]").length;
    if (loadingCount > 0) {
        parentElement.attr("data-is-loading", loadingCount);
        parentElement.addClass("suspended");
    }
    parentElement.addClass("suspense");
    return element("div", {},
        parentElement,
        loadingElement
    );
}

function showError(title, xhr) {
    var serverErrorMessage = xhr.responseJSON == null ? "No response from server." : xhr.responseJSON.message;
    console.error(title + "\n" + serverErrorMessage);
    var notification = $("<div class=\"toast\" role=\"alert\" aria-live=\"assertive\" aria-atomic=\"true\" style=\"min-width: 30rem\">"
            + "<div class=\"toast-header bg-danger\">"
            + "<strong class=\"mr-auto text-dark\">Error</strong>"
            + "<button type=\"button\" class=\"ml-2 mb-1 close\" data-dismiss=\"toast\" aria-label=\"Close\">"
            + "<span aria-hidden=\"true\">&times;</span>"
            + "</button>"
            + "</div>"
            + "<div class=\"toast-body\"><p>" + title + "</p><pre><code>" + serverErrorMessage + "</code></pre></div>"
            + "</div>");
    $("#notificationPanel").append(notification);
    notification.toast({delay: 30000});
    notification.toast('show');
}

$(document).ready( function() {
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
        }
    });
    // Extend jQuery to support $.put() and $.delete()
    jQuery.each( [ "put", "delete" ], function( i, method ) {
        jQuery[method] = function (url, data, callback, type) {
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }
            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });
    initModal();
    refresh();
});

// ****************************************************************************
// Name Generator
// ****************************************************************************

let nameSequence = 0;

function randomName() {
    const FIRST_NAMES = ["Amy", "Bill", "Chris", "Dennis", "Hope", "Eve", "Frank", "Ivana", "Julianna", "Manci", "Olivia", "Sarah"];
    const MIDDLE_INITALS = [...Array('Z'.charCodeAt(0) - 'A'.charCodeAt(0) + 1).keys()].map(i => String.fromCharCode(i + 'A'.charCodeAt(0)));
    const LAST_NAMES = ["Cole", "Zhang", "Smith"];
    const out = `${FIRST_NAMES[nameSequence % FIRST_NAMES.length]} ${MIDDLE_INITALS[Math.floor(nameSequence / FIRST_NAMES.length)]} ${LAST_NAMES[Math.floor((nameSequence / FIRST_NAMES.length) / LAST_NAMES.length)]}`
    nameSequence = (nameSequence + 1) % (FIRST_NAMES.length * LAST_NAMES.length);
    return out;
}

// ****************************************************************************
// TangoColorFactory
// ****************************************************************************

const SEQUENCE_1 = [0x8AE234, 0xFCE94F, 0x729FCF, 0xE9B96E, 0xAD7FA8];
const SEQUENCE_2 = [0x73D216, 0xEDD400, 0x3465A4, 0xC17D11, 0x75507B];

var colorMap = new Map;
var nextColorCount = 0;

function pickColor(object) {
    let color = colorMap[object];
    if (color !== undefined) {
        return color;
    }
    color = nextColor();
    colorMap[object] = color;
    return color;
}

function nextColor() {
    let color;
    let colorIndex = nextColorCount % SEQUENCE_1.length;
    let shadeIndex = Math.floor(nextColorCount / SEQUENCE_1.length);
    if (shadeIndex === 0) {
        color = SEQUENCE_1[colorIndex];
    } else if (shadeIndex === 1) {
        color = SEQUENCE_2[colorIndex];
    } else {
        shadeIndex -= 3;
        let floorColor = SEQUENCE_2[colorIndex];
        let ceilColor = SEQUENCE_1[colorIndex];
        let base = Math.floor((shadeIndex / 2) + 1);
        let divisor = 2;
        while (base >= divisor) {
            divisor *= 2;
        }
        base = (base * 2) - divisor + 1;
        let shadePercentage = base / divisor;
        color = buildPercentageColor(floorColor, ceilColor, shadePercentage);
    }
    nextColorCount++;
    return "#" + color.toString(16);
}

function buildPercentageColor(floorColor, ceilColor, shadePercentage) {
    let red = (floorColor & 0xFF0000) + Math.floor(shadePercentage * ((ceilColor & 0xFF0000) - (floorColor & 0xFF0000))) & 0xFF0000;
    let green = (floorColor & 0x00FF00) + Math.floor(shadePercentage * ((ceilColor & 0x00FF00) - (floorColor & 0x00FF00))) & 0x00FF00;
    let blue = (floorColor & 0x0000FF) + Math.floor(shadePercentage * ((ceilColor & 0x0000FF) - (floorColor & 0x0000FF))) & 0x0000FF;
    return red | green | blue;
}