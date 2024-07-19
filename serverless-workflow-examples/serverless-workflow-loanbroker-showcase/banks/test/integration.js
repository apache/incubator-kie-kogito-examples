'use strict';
const { start } = require('faas-js-runtime');
const request = require('supertest');

const func = require('..').handle;
const test = require('tape');

const Spec = {
  version: 'ce-specversion',
  type: 'ce-type',
  id: 'ce-id',
  source: 'ce-source'
};

const data = { "amount": 300000, "term": 30, "credit": { "score": 700, "history": 15 } }

const errHandler = t => err => {
  t.error(err);
  t.end();
};

test('Integration: handles a valid cloudevents+json event', t => {
  inject_env(t);

  const ceData = {
    specversion: "1.0",
    type: "com.example.someevent",
    source: "/mycontext",
    subject: null,
    id: "C234-1234-1234",
    time: "2018-04-05T17:31:00Z",
    datacontenttype: "application/json",
    kogitoprocinstanceid: "12345",
    data: data
  };

  start(func).then(server => {
    t.plan(5);
    request(server)
      .post('/')
      .send(ceData)
      .set("Content-Type", "application/cloudevents+json")
      .expect(200)
      .expect('Content-Type', /json/)
      .end((err, result) => {
        t.error(err, 'No error');
        t.ok(result);
        t.deepEqual(result.body.bankId, "BankUnitTest");
        t.equal(result.headers['ce-type'], 'kogito.serverless.loanbroker.bank.offer');
        t.equal(result.headers['ce-source'], '/kogito/serverless/loanbroker/bank/BankUnitTest');
        t.end();
        server.close();
      });
  }, errHandler(t));
});

test('Integration: handles a valid event', t => {
  inject_env(t);

  start(func).then(server => {
    t.plan(5);
    request(server)
      .post('/')
      .send(data)
      .set(Spec.id, '01234')
      .set(Spec.source, '/test')
      .set(Spec.type, 'com.example.cloudevents.test')
      .set(Spec.version, '1.0')
      .set("ce-kogitoprocinstanceid", "12345")
      .expect(200)
      .expect('Content-Type', /json/)
      .end((err, result) => {
        t.error(err, 'No error');
        t.ok(result);
        t.deepEqual(result.body.bankId, "BankUnitTest");
        t.equal(result.headers['ce-type'], 'kogito.serverless.loanbroker.bank.offer');
        t.equal(result.headers['ce-source'], '/kogito/serverless/loanbroker/bank/BankUnitTest');
        t.end();
        server.close();
      });
  }, errHandler(t));
});

/**
 * Inject the required env vars
 * @param {test.Test} t 
 */
function inject_env(t) {
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
}