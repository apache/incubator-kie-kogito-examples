'use strict';

const { start } = require('faas-js-runtime');
const request = require('supertest');

const func = require('..').handle;
const test = require('tape');

const errHandler = t => err => {
  t.error(err);
  t.end();
};

test('Integration: handles an HTTP GET', t => {
  start(func).then(server => {
    t.plan(2);
    request(server)
      .get('/?SSN=123-45-6789')
      .expect(200)
      .expect('Content-Type', /json/)
      .end((err, res) => {
        t.error(err, 'No error');
        t.deepEqual(res.body.SSN, "123-45-6789");
        t.end();
        server.close();
      });
  }, errHandler(t));
});

test('Integration: responds with error code if it is not GET', t => {
  start(func).then(server => {
    t.plan(1);
    request(server)
      .put('/')
      .send({ name: 'tiger' })
      .expect(200)
      .expect('Content-Type', /json/)
      .end((err, res) => {
        t.deepEqual(res.body, { message: 'Route PUT:/ not found', error: 'Not Found', statusCode: 404 });
        t.end();
        server.close();
      });
  }, errHandler(t));
});
