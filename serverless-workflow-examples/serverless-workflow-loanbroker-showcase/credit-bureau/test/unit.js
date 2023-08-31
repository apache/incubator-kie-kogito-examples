/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
