/**
 * Renders the display of a flight
 * @param {object} flight - The flight to display
 * 
 * @returns jQuery
 */
function renderFlight(flight) {
    return element("div", {},
      JSON.stringify(flight)
    );
}

function refresh() {
    $("#flights-container").empty();
    $.getJSON("/rest/flights", flights => {
        flights.forEach(flight => renderFlight(flight).appendTo("#flights-container"));
    });
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
            out.prop(prop, props[prop]);
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
            'Accept': 'application/json'
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
