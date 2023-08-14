package org.acme.loanbroker;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import org.acme.loanbroker.domain.QuotesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.vertx.ConsumeEvent;

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
