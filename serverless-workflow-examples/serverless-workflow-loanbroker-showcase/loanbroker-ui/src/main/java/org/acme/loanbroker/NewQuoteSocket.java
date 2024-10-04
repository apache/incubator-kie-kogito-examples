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
package org.acme.loanbroker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.acme.loanbroker.domain.QuotesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/socket/quote/new")
@ApplicationScoped
public class NewQuoteSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(NewQuoteSocket.class);

    @Inject
    ObjectMapper mapper;
    private Queue<Session> sessions = new ConcurrentLinkedQueue<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @ConsumeEvent("new-quote")
    void consumeNewQuoteAndBroadcast(QuotesResponse quotes) throws JsonProcessingException {
        LOGGER.info("Broadcasting a new quote response {}", quotes);
        for (Session s : sessions) {
            s.getAsyncRemote().sendText(mapper.writeValueAsString(quotes), result -> {
                if (result.getException() != null) {
                    LOGGER.error("Failed to broadcast message with contents {}", quotes, result.getException());
                }
            });
        }
    }

}
