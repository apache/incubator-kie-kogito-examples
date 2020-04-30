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
            element("button", { class: "btn btn-secondary", style: "margin: 5px;", onClick: () => generatePassengersForFlight(flight) }, "Generate passenger list")
        ]: []
        header = element("div", {},
            element("h5", {}, getFlightName(flight)),
            element("button", {
                class: "btn btn-secondary",
                role: "button",
                style: "margin: 5px;",
                "data-flight": flight.id,
                "data-action": "add",
                "data-toggle": "modal",
                "data-target": "#add-passenger-modal",
            },
            "Add passenger"
            ),
            ...generatePassengerElement,
            element("button", {
                class: "btn btn-primary",
                role: "button",
                style: "margin: 5px;",
                onClick: () => {
                    $.post(`/rest/flights/${flight.id}/finalizePassengerList/${finalizePassengerListTasks[0]}`, JSON.stringify({}), () => {
                        const refreshFlight = () => {
                            $.getJSON(`/rest/flights/${flight.id}`, flight => {
                                $.getJSON(`/rest/flights/${flight.id}/tasks`, tasks => {
                                    $(`#${flight.id} > *`).replaceWith(renderFlight(flight, tasks));
                                }).then(() => {
                                    if (flight.isSolving) {
                                        setTimeout(refreshFlight, 500);
                                    }
                                });
                            });
                        };
                        refreshFlight();
                    });
                }
            }, "Finalize passenger list")
        );
    }
    else if (flight.isSolving) {
        header = element("div", {},
            element("h5", {}, getFlightName(flight)), element("h6", {}, "Solving..."), element("span", {}, `Score: ${getFlightScore(flight.flight)}`));
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
        }, "Finalize seat assignments")] : [];

        header = element("div", {},
            element("h5", {}, getFlightName(flight)),
            element("div", {}, `Score: ${getFlightScore(flight.flight)}`),
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
    const passengerRequestString = passenger => `${passenger.name}, Is Paying for Seat? ${passenger.paidForSeat? "Yes, Seat " + getSeatName(passenger.seat) : "No"}`;
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
                            element("i", { class: "fas fa-user", style: "margin-right: 10px;" }, ""),
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
                flightSeats(flight, ({ passenger }) => (passenger)? element("div", {}, passenger.name) : element("div", {}, "")),
                // Approved Passenger List
                element("div", { class: "card" },
                    element("h5", {}, `Passenger list (${flight.flight.passengerList.length} passenger${(flight.flight.passengerList.length == 1)? "" : "s"})`),
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
                            element("i", { class: "fas fa-user", style: "margin-right: 10px;" }, ""),
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
    const seatToIndictment = sortedSeatList.map(seat => getSeatIndictment(seat, flight.flight.passengerList));
    const seatColor = (index) => (seatToIndictment[index].score.hard < 0)? 'var(--global-color-unavailable)' : 
        (seatToIndictment[index].score.soft < 0)? 'var(--global-color-undesired)' : 
        (seatToIndictment[index].matchPreference)? 'var(--global-color-desired)' :
        'var(--global-color-normal)';
    const seatTooltip = (seat, index) => (seatToIndictment[index].indictments.length === 0)? `The seat ${seat.name} does not violate any constraints.` :
        seatToIndictment[index].indictments.map(indictment => `${indictment.name}: ${indictment.description} (${indictment.score.hard} Hard/${indictment.score.soft} Soft)`)
        .reduce((prev, curr) => `${prev};\n${curr}`);
        
    const emergencyExitRow = rowToGridRow(sortedSeatList.find(seat => seat.emergencyExitRow).row);
    
    return element("div", { style: `display: grid;
        grid-column: 2;
        grid-auto-flow: dense;
        grid-template-rows: repeat(${2*flight.flight.seatRowSize}, minmax(50px, 1fr));
        grid-template-columns: repeat(${flight.flight.seatColumnSize + 1}, minmax(100px, 1fr));
        column-gap: 15px;
        padding-left: 15px;
        padding-right: 15px;
        width: min-content;
        border: 1px solid;
        ` }, ...sortedSeatList.map((seat, index) => element(
            "span", {
              style: `grid-row: ${rowToGridRow(seat.row)};
                      grid-column: ${columnToGridColumn(seat.column)};
                      margin-top: 15px;
                      background-color: ${seatColor(index)};
                      text-align: center;
                      `,
              title: seatTooltip(seat, index)
            },
            element("span", {
                class: "fas fa-couch",
                style: "margin-right: 5px;"
            }, ""),
            element("span", {}, seat.name)
        )),
        ...sortedSeatList.map((seat, index) => flightSeatToElementMap({flight: flight.flight, seat: seat, passenger: flight.flight.passengerList.find(passenger => passenger.seat !== null &&
            passenger.seat.row === seat.row && passenger.seat.column === seat.column) } ).css({
                "grid-row": String(rowToGridRow(seat.row) + 1),
                "grid-column": String(columnToGridColumn(seat.column)),
                "background-color": seatColor(index),
                "text-align": "center",
                "margin-bottom": "15px"
            }).attr("title", seatTooltip(seat, index))),
        element("div", {
            style: `grid-column: 1 / -1;
                    grid-row: ${emergencyExitRow} / span 2;
                    margin-top: 15px;
                    margin-bottom: 15px;
                    margin-left: -15px;
                    margin-right: -15px;
                    border-top: 2px dashed;
                    border-bottom: 2px dashed;
                    pointer-events: none;
                   `
        }, "")
    );
}

function getFlightScore(flight) {
    return flight.score;
}

function getSeatIndictment(seat, passengerList) {
    const out = {
        score: {
            hard: 0,
            soft: 0
        },
        indictments: []
    };
    const passengersInSeat = passengerList.filter(passenger => passenger.seat !== null && passenger.seat.name === seat.name);
    if (passengersInSeat.length > 1) {
        out.score.hard -= 1;
        out.indictments.push({
            name: "Seat conflict",
            description: `The seat ${seat.name} has multiple passengers: ${passengersInSeat.map(passenger => passenger.name)}.`,
            score: { hard: -1, soft: 0 }
        });
    }
    passengersInSeat.forEach(passenger => {
        if (seat.emergencyExitRow && !passenger.emergencyExitRowCapable) {
            out.score.hard -= 1;
            out.indictments.push({
                name: "Emergency exit row has incapable passenger",
                description: `The seat ${seat.name} is in the emergency exit row but the passenger ${passenger.name} cannot assist in an emergency.`,
                score: { hard: -1, soft: 0 }
            });
        }
        if (passenger.seatTypePreference !== null && passenger.seatTypePreference !== seat.seatType) {
            out.score.soft -= 1;
            out.indictments.push({
                name: "Seat type preference",
                description: `The passenger ${passenger.name} prefers ${passenger.seatTypePreference} seats but got a ${seat.seatType} seat.`,
                score: { hard: 0, soft: -1 }
            });
        }
        else if (passenger.seatTypePreference !== null && passenger.seatTypePreference === seat.seatType) {
            out.matchPreference = true;
            out.indictments.push({
                name: "Seat type preference",
                description: `The passenger ${passenger.name} prefers ${passenger.seatTypePreference} seats and got a ${seat.seatType} seat.`,
                score: { hard: 0, soft: 0 }
            });
        }
    });
    return out;
}

let myFlights = [];
let flightOrder = [];

function refresh() {
    $.getJSON("/rest/flights", flights => {
        const toRemove = [...flightOrder];
        const toAdd = [];
        flights.forEach(flight => {
            if (!flightOrder.includes(flight.id)) {
                flightOrder.push(flight.id);
                toAdd.push(flight.id);
            }
            else {
                toRemove.splice(toRemove.indexOf(flight.id), 1);
            }
        });
        toRemove.forEach(flight => {
            flightOrder.splice(flightOrder.indexOf(flight), 1);
            $(`#${flight}`).remove();
        });
        toAdd.forEach(flightId => element("div", { id: flightId }, element("span", {})).appendTo("#flights-container"));
        myFlights = flights;
        
        flights.forEach(flight => {
            $.getJSON(`/rest/flights/${flight.id}/tasks`, tasks => {
                $(`#${flight.id} > *`).replaceWith(renderFlight(flight, tasks));
            });
        });
    });
}

function getFlightName(flight) {
    return `${flight.flight.origin} -> ${flight.flight.destination}, departing on ${moment(flight.flight.departureDateTime).format("LLL")}`;
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
    const SEAT_TYPE_PREFERENCE_CHOICES = ["NONE", "WINDOW", "AISLE"];
    const passengersToAddList = [];
    for (let i = 0; i < flight.flight.seatList.length * 0.8; i++) {
        passengersToAddList.push(JSON.stringify({
            name: randomName(),
            seatTypePreference: SEAT_TYPE_PREFERENCE_CHOICES[Math.floor(Math.random() * SEAT_TYPE_PREFERENCE_CHOICES.length)],
            emergencyExitRowCapable: Math.random() < 0.8,
            paidForSeat: false
        }));
    }
    Promise.all(passengersToAddList.map(newPassengerRequest => $.post(`/rest/flights/${flight.id}/newPassengerRequest`, newPassengerRequest, () => {}, "json"))).then(() => {
        console.log("Hi");
        $.getJSON(`/rest/flights/${flight.id}/tasks`, tasks => {
            Promise.all(findTasks("approveDenyPassenger", tasks).map(task => $.post(`/rest/flights/${flight.id}/approveDenyPassenger/${task}`, JSON.stringify({
                isPassengerApproved: true
            }), () => {}, "json"))).then(() => refresh());
        });
    });
}

function initModal() {
    $("#new-flight-modal").on('show.bs.modal', () => {
        const newFlightAction = $("#new-flight-action");
        newFlightAction.unbind();
        const modal = $(this);

        $('.modal-title').text("Create New Flight");
        $('#origin').val("JFK");
        $('#destination').val("SFO");
        $("#departureDate").val(moment().add(1, 'day').startOf('day').format("YYYY-MM-DD"));
        $("#departureTime").val(moment().add(1, 'day').startOf('minute').format("HH:mm"));

        $("#seatRowSize").val(4);
        $("#seatColumnSize").val(6);

        newFlightAction.off('click').click(() => {
            const origin = $('#origin').val();
            const destination = $('#destination').val();
            const departureDate = $('#departureDate').val();
            const departureTime = $('#departureTime').val();
            const departureDateTime = departureDate + "T" + departureTime;
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
        modal.find('.modal-content').css({ width: 'min-content' });
        modal.find('.modal-title').text(`Add Passenger to Flight ${getFlightName(flightObject)}`);
        modal.find('#name').val(randomName());
        modal.find('#seatTypePreference').val("NONE");
        modal.find('#emergencyExitRowCapable').prop('checked', false);
        modal.find('#paidForSeat').prop('checked', false);
        
        modal.find('#seatPicker').replaceWith(element("div", { id: "seatPicker", class: "seat-picker hide-seat-picker" }, flightSeats(flightObject, ({seat, passenger}) => element("input",
            { type: "radio", class: "form-control", name: "flight-seat", value: seat.row + ";" + seat.column, disabled: passenger !== undefined, hidden: passenger !== undefined }))));

        modal.find("#paidForSeat").off('click').click(() => $("#add-passenger-modal").find('#seatPicker').toggleClass("hide-seat-picker"));


        addPassengerAction.off('click').click(() => {
            const name = $('#name').val();
            const seatTypePreference = $('#seatTypePreference').val();
            const emergencyExitRowCapable = $('#emergencyExitRowCapable').prop( "checked" );
            const paidForSeat = $('#paidForSeat').prop( "checked" );
            const seat = (paidForSeat)? $('input[name="flight-seat"]:checked').val() : null;

            const newPassengerRequest = JSON.stringify({
                    name,
                    seatTypePreference,
                    emergencyExitRowCapable,
                    paidForSeat,
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