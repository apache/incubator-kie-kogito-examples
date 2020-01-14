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
        header = element("div", {},
            element("h5", {}, flight.id),
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
            element("h5", {}, flight.id),
            ...finalizeSeatAssignmentButton
        );
    }
    return element("div", {},
      element("div", {},
            header,
            element("div", { style: "display: grid; grid-template-columns: 1fr 1fr;"},
                element("div", { style: "grid-column: 1;" },
                    ...getPassengersToApproveDeny(flight, tasks).map(task => element(
                        "div", {},
                        element("span", {}, task.passenger.name),
                        element("button", {
                            onClick: () => {
                                $.post(`/rest/flights/${flight.id}/approveDenyPassenger/${task.id}`, JSON.stringify({
                                    passenger: task.passenger,
                                    isPassengerApproved: true
                                }), () => {
                                    refresh();
                                });
                            }
                    }, "Approve"),
                    element("button", {
                        onClick: () => {
                            $.post(`/rest/flights/${flight.id}/approveDenyPassenger/${task.id}`, JSON.stringify({
                                passenger: task.passenger,
                                isPassengerApproved: false
                            }), () => {
                                refresh();
                            });
                        }
                    }, "Deny")
            ))),
            element("div", {}, 
                element("div", { style: `display: grid;
                    grid-column: 2;
                    grid-template-rows: repeat(${2*flight.flight.seatRowSize}, 1fr);
                    grid-template-columns: repeat(${flight.flight.seatColumnSize}, 1fr);
                    justify-items: center;
                    align-items: center;
                    border: 1px solid;
                    ` }, ...flight.flight.seatList.map(seat => element(
                      "span", { style: `grid-row: ${2*seat.row + 1}; grid-column: ${seat.column + 1}`},
                      seat.name)
                    ),
                    ...flight.flight.passengerList.map(passenger => (passenger.seat !== null)? element(
                        "span", { style: `grid-row: ${2*passenger.seat.row + 2}; grid-column: ${passenger.seat.column + 1}`},
                        passenger.name
                    ) : element("div", { hidden: true }, ""))
                ),
                element("div", {}, ...flight.flight.passengerList.map(passenger => element(
                    "div", {}, passenger.name
                )))
              )),
            element("div", {},
                element("button", {
                    type: "button",
                    "data-toggle": "collapse",
                    "data-target": `#flight-${flight.id}-json`,
                    "aria-expanded": "false",
                    "aria-controls": `#flight-${flight.id}-json`
                }, "Show Flight JSON"),
                element("div", {
                    class: "collapse",
                    id: `flight-${flight.id}-json`
                }, element("div", { class: "card card-body" }, JSON.stringify(flight))),
            )
    ));
}

function refresh() {
    $("#flights-container").empty();
    $.getJSON("/rest/flights", flights => {
        flights.forEach(flight => {
            $.getJSON(`/rest/flights/${flight.id}/tasks`, tasks => {
                renderFlight(flight, tasks).appendTo("#flights-container");
            });
        });
    });
}

function findTasks(taskName, tasks) {
    return Object.keys(tasks).filter(key => tasks[key] === taskName);
}

function getPassengersToApproveDeny(flight, tasks) {
    const out = findTasks("approveDenyPassenger", tasks).map(task => {
        return $.getJSON(`/rest/flights/${flight.id}/approveDenyPassenger/${task}`).responseJSON;
    });
    return out;
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

        $("#seatRowSize").val(6);
        $("#seatColumnSize").val(8);

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
        var addPassengerAction = $("#add-passenger-action");
        addPassengerAction.unbind();
        var modal = $("#add-passenger-modal");
        modal.find('.modal-title').text(`Add Passenger to Flight ${flight}`);
        modal.find('#name').val(randomName());
        modal.find('#seatTypePreference').val("NONE");
        modal.find('#emergencyExitRowCapable').val(true);
        modal.find('#payedForSeat').val(false);


        addPassengerAction.click(() => {
            const name = $('#name').val();
            const seatTypePreference = $('#seatTypePreference').val();
            const emergencyExitRowCapable = $('#emergencyExitRowCapable').val();
            const payedForSeat = $('#payedForSeat').val();

            const newPassengerRequest = JSON.stringify({
                    name,
                    seatTypePreference,
                    emergencyExitRowCapable,
                    payedForSeat,
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
        },
        async: false
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

function randElement(list) {
    return list[Math.floor(Math.random() * list.length)];
}

function randomName() {
    const FIRST_NAMES = ["Amy", "Bill", "Chris", "Dennis", "Hope", "Eve", "Frank", "Ivana", "Julianna", "Manci", "Olivia", "Sarah"];
    const LAST_NAMES = ["Cole", "Zhang", "Smith"];
    return `${randElement(FIRST_NAMES)} ${randElement(LAST_NAMES)}`
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
