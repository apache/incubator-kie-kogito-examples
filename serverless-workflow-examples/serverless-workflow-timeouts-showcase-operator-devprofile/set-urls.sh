#!/bin/bash
#
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#


CALLBACK_STATE_TIMEOUTS_WF=callbackstatetimeouts
EVENT_STATE_TIMEOUTS_WF=eventstatetimeouts
SWITCH_STATE_TIMEOUTS_WF=switchstatetimeouts
WORKFLOW_TIMEOUTS_WF=workflowtimeouts

CALLBACK_STATE_TIMEOUTS_ROOT=`minikube service $CALLBACK_STATE_TIMEOUTS_WF -n timeouts-showcase  --url`
EVENT_STATE_TIMEOUTS_ROOT=`minikube service $EVENT_STATE_TIMEOUTS_WF -n timeouts-showcase  --url`
SWITCH_STATE_TIMEOUTS_ROOT=`minikube service $SWITCH_STATE_TIMEOUTS_WF -n timeouts-showcase  --url`
WORKFLOW_TIMEOUTS_ROOT=`minikube service $WORKFLOW_TIMEOUTS_WF -n timeouts-showcase  --url`

CALLBACK_STATE_TIMEOUTS_URL="$CALLBACK_STATE_TIMEOUTS_ROOT/$CALLBACK_STATE_TIMEOUTS_WF"
EVENT_STATE_TIMEOUTS_URL="$EVENT_STATE_TIMEOUTS_ROOT/$EVENT_STATE_TIMEOUTS_WF"
SWITCH_STATE_TIMEOUTS_URL="$SWITCH_STATE_TIMEOUTS_ROOT/$SWITCH_STATE_TIMEOUTS_WF"
WORKFLOW_TIMEOUTS_URL="$WORKFLOW_TIMEOUTS_ROOT/$WORKFLOW_TIMEOUTS_WF"


echo "Setting workflows env variables to:"
echo "CALLBACK_STATE_TIMEOUTS_URL=$CALLBACK_STATE_TIMEOUTS_URL"
echo "EVENT_STATE_TIMEOUTS_URL=$EVENT_STATE_TIMEOUTS_URL"
echo "SWITCH_STATE_TIMEOUTS_URL=$SWITCH_STATE_TIMEOUTS_URL"
echo "WORKFLOW_TIMEOUTS_URL=$WORKFLOW_TIMEOUTS_URL"
