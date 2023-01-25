'use strict';

const func = require('..').handle;
const test = require('tape');
const { CloudEvent } = require('cloudevents');

const data = { "amount": 300000, "term": 30, "credit": { "score": 700, "history": 15 } }

// A valid event includes id, type and source at a minimum.
const cloudevent = new CloudEvent({
  id: '01234',
  type: 'com.example.cloudevents.test',
  source: '/test',
  kogitoprocinstanceid: "12345AAA",
  data
});

// Ensure that the function completes cleanly when passed a valid event.
test('Unit: handles a valid event', async t => {
  t.teardown(() => {
    delete process.env.BANK_ID;
    delete process.env.MAX_LOAN_AMOUNT;
    delete process.env.MIN_CREDIT_SCORE;
    delete process.env.BASE_RATE;
  });

  process.env.BANK_ID = "BankUnitTest";
  process.env.MAX_LOAN_AMOUNT = "500000";
  process.env.MIN_CREDIT_SCORE = "300";
  process.env.BASE_RATE = "3";

  t.plan(4);

  // Invoke the function with the valid event, which should complete without error.
  const result = await func(new MockContext(cloudevent), data);
  t.ok(result);
  t.equal(JSON.parse(result.body).bankId, "BankUnitTest");
  t.equal(result.headers['ce-type'], 'kogito.serverless.loanbroker.bank.offer');
  t.equal(result.headers['ce-source'], '/kogito/serverless/loanbroker/bank/BankUnitTest');
  t.end();
});

test('Unit: handles a valid event as byte array', async t => {
  t.teardown(() => {
    delete process.env.BANK_ID;
    delete process.env.MAX_LOAN_AMOUNT;
    delete process.env.MIN_CREDIT_SCORE;
    delete process.env.BASE_RATE;
  });

  process.env.BANK_ID = "BankUnitTest";
  process.env.MAX_LOAN_AMOUNT = "500000";
  process.env.MIN_CREDIT_SCORE = "300";
  process.env.BASE_RATE = "3";

  t.plan(4);

  const cloudEventBinary = cloudevent;
  const dataArray = new Uint8Array(new TextEncoder().encode(JSON.stringify(data)).buffer);
  cloudEventBinary.data = {
    type: "Buffer",
    data: [...dataArray]
  };

  const result = await func(new MockContext(cloudEventBinary), dataArray);
  t.ok(result);
  t.equal(JSON.parse(result.body).bankId, "BankUnitTest");
  t.equal(result.headers['ce-type'], 'kogito.serverless.loanbroker.bank.offer');
  t.equal(result.headers['ce-source'], '/kogito/serverless/loanbroker/bank/BankUnitTest');
  t.end();
});

class MockContext {
  cloudevent;

  constructor(cloudevent) {
    this.cloudevent = cloudevent;
    this.log = { info: console.log, debug: console.debug }
  }

  cloudEventResponse(data) {
    return new CloudEvent({
      data
    })
  }
}
