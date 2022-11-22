const { CloudEvent, HTTP } = require('cloudevents');

/**
 * Your CloudEvent handling function, invoked with each request.
 * This example function logs its input, and responds with a CloudEvent
 * which echoes the incoming event data
 *
 * It can be invoked with 'func invoke'
 * It can be tested with 'npm test'
 *
 * @param {Context} context a context object.
 * @param {object} context.body the request body if any
 * @param {object} context.query the query string deserialzed as an object, if any
 * @param {object} context.log logging object with methods for 'info', 'warn', 'error', etc.
 * @param {object} context.headers the HTTP request headers
 * @param {string} context.method the HTTP request method
 * @param {string} context.httpVersion the HTTP protocol version
 * See: https://github.com/knative-sandbox/kn-plugin-func/blob/main/docs/guides/nodejs.md#the-context-object
 * @param {CloudEvent} event the CloudEvent
 */
const handle = async (context, event) => {
  console.log("context");
  console.log(JSON.stringify(context, null, 2));

  console.log("event");
  console.log(JSON.stringify(event, null, 2));

  if (context.cloudevent.data === 'undefined' || typeof context.cloudevent.data !== 'object') {
    console.warn("Received CloudEvent without data, aborting.");
    return { statusCode: 400, statusMessage: 'Invalid CloudEvent' };
  }

  const requestId = context.cloudevent.kogitoprocinstanceid;
  const bankId = process.env.BANK_ID;
  const eventType = "kogito.serverless.loanbroker.bank.offer";

  var data = JSON.parse(JSON.stringify(context.cloudevent.data));

  if (data && data.type === "Buffer") {
    data = JSON.parse(new TextDecoder().decode(new Uint8Array(data.data)));
  }

  console.log("Data is: " + JSON.stringify(data));

  const response = bankQuote(data, bankId);

  if (response != null) {
    return HTTP.binary(new CloudEvent({
      source: "/kogito/serverless/loanbroker/bank/" + bankId,
      type: eventType,
      data: response,
      kogitoprocrefid: requestId
    }));
  }
}

function calcRate(amount, term, score, history) {
  if (amount <= process.env.MAX_LOAN_AMOUNT && score >= process.env.MIN_CREDIT_SCORE) {
    return parseFloat(process.env.BASE_RATE) + Math.random() * ((1000 - score) / 100.0);
  }
}

bankQuote = (quoteRequest, bankId) => {
  const rate = calcRate(quoteRequest.amount, quoteRequest.term, quoteRequest.credit.score, quoteRequest.credit.history);

  if (rate) {
    console.log('%s offering Loan at %f', bankId, rate);
    return { "rate": rate, "bankId": bankId };
  } else {
    console.log('%s rejecting Loan', bankId);
    return null;
  }
}

// Export the function
module.exports = { handle };
