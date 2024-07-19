'use strict';

const func = require('..').handle;
const test = require('tape');

const fixture = { log: { info: console.log } };

test('Unit: handles an HTTP GET', async t => {
  t.plan(3);
  const ssn = "123-45-6789";
  // Invoke the function, which should complete without error.
  const result = await func({ ...fixture, method: 'GET', query: { SSN: ssn} });
  t.deepEqual(result.body.SSN, ssn);
  t.false(result.body.history === "");
  t.false(result.body.score === "");
  t.end();
});

test('Unit: handles an HTTP POST', async t => {
  t.plan(1);
  // Invoke the function, which should complete without error.
  const result = await func({ ...fixture, method: 'POST', body: { name: 'tiger' } });
  t.deepEqual(result, { statusCode: 405, statusMessage: 'Method not allowed' });
  t.end();
});

test('Unit: responds with error code if neither GET or POST', async t => {
  t.plan(1);
  const result = await func(fixture);
  t.deepEqual(result, { statusCode: 405, statusMessage: 'Method not allowed' });
  t.end();
});
