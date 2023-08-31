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
/**
 * Your HTTP handling function, invoked with each request. This is an example
 * function that echoes its input to the caller, and returns an error if
 * the incoming request is something other than an HTTP POST or GET.
 *
 * In can be invoked with 'func invoke'
 * It can be tested with 'npm test'
 *
 * @param {Context} context a context object.
 * @param {object} context.body the request body if any
 * @param {object} context.query the query string deserialized as an object, if any
 * @param {object} context.log logging object with methods for 'info', 'warn', 'error', etc.
 * @param {object} context.headers the HTTP request headers
 * @param {string} context.method the HTTP request method
 * @param {string} context.httpVersion the HTTP protocol version
 * See: https://github.com/knative-sandbox/kn-plugin-func/blob/main/docs/guides/nodejs.md#the-context-object
 */
const handle = async (context) => {
  context.log.info(JSON.stringify(context, null, 2));

  if (context.method === 'GET') {
    const min_score = 300
    const max_score = 900

    var ssn_regex = new RegExp("^\\d{3}-\\d{2}-\\d{4}$");
    if (ssn_regex.test(context.query.SSN)) {
      context.body = {
        SSN: context.query.SSN,
        score: getRandomInt(min_score, max_score),
        history: getRandomInt(1, 30)
      }
    } else {
      return { statusCode: 400, statusMessage: 'Invalid SSN: ' + context.query.SSN };
    }

    return {
      body: context.body,
    }
  } else {
    return { statusCode: 405, statusMessage: 'Method not allowed' };
  }
}

function getRandomInt(min, max) {
  return min + Math.floor(Math.random() * (max - min));
}

// Export the function
module.exports = { handle };
