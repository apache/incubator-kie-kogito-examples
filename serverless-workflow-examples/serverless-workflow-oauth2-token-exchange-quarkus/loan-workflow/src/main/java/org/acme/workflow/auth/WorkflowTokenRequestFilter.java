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
package org.acme.workflow.auth;

import io.quarkus.arc.Arc;
import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.cache.CaffeineCache;
import io.quarkus.oidc.client.*;
import io.quarkus.oidc.token.propagation.AccessTokenRequestFilter;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.security.credential.TokenCredential;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.resteasy.microprofile.client.impl.MpClientInvocation;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorkflowTokenRequestFilter extends AccessTokenRequestFilter {

    @Inject
    Instance<TokenCredential> accessToken;

    @CacheName("my-cache")
    Cache refreshTokensCache;

    OidcClient exchangeTokenClient;
    String exchangeTokenProperty;

    @PostConstruct
    public void initExchangeTokenClient() {
        if (isExchangeToken()) {
            OidcClients clients = Arc.container().instance(OidcClients.class).get();
            String clientName = getClientName();
            exchangeTokenClient = clientName != null ? clients.getClient(clientName) : clients.getClient();
            OidcClientConfig.Grant.Type exchangeTokenGrantType = ConfigProvider.getConfig().getValue("quarkus.oidc-client." + (clientName != null ? clientName + "." : "") + "grant.type", OidcClientConfig.Grant.Type.class);
            if (exchangeTokenGrantType == OidcClientConfig.Grant.Type.EXCHANGE) {
                exchangeTokenProperty = "subject_token";
            } else if (exchangeTokenGrantType == OidcClientConfig.Grant.Type.JWT) {
                exchangeTokenProperty = "assertion";
            } else {
                throw new ConfigurationException("Token exchange is required but OIDC client is configured " + "to use the " + exchangeTokenGrantType.getGrantType() + " grantType");
            }
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        final String authTokenFromCtx = acquireTokenCredentialFromCtx(requestContext);
        if (authTokenFromCtx.isEmpty()) {
            if (verifyTokenInstance(requestContext, accessToken)) {
                propagateToken(requestContext, exchangeTokenIfNeeded(accessToken.get().getToken()));
            }
        } else {
            propagateToken(requestContext, exchangeTokenIfNeeded(authTokenFromCtx));
        }
    }

    private String exchangeTokenIfNeeded(String token) {
        if (exchangeTokenClient != null) {
            Tokens tokens;
            try {
                // more dynamic parameters can be configured if required
                // cache exchangedTokens to avoid roundtrips, for this to work we have to introspect the token and verify if it's valid or expired
                // see https://quarkus.io/guides/security-oidc-bearer-token-authentication#accessing-jwt-claims
                tokens = exchangeTokenClient.getTokens(Collections.singletonMap(exchangeTokenProperty, token)).await().indefinitely();
                // Lame implementation
                refreshTokensCache.as(CaffeineCache.class).put(token, CompletableFuture.completedFuture(tokens.getRefreshToken()));
                return tokens.getAccessToken();
            } catch (OidcClientException e) {
                // no refresh tokens, nothing to do.
                if (refreshTokensCache.get(token, k -> null).await().indefinitely() == null) {
                    throw e;
                }
                // catch, if invalid token try refreshing
                final String refreshToken = (String) refreshTokensCache.get(token, k -> null).await().indefinitely();
                tokens = exchangeTokenClient.refreshTokens(refreshToken).await().indefinitely();
                // Lame implementation
                refreshTokensCache.as(CaffeineCache.class).put(token, CompletableFuture.completedFuture(tokens.getRefreshToken()));
                return tokens.getAccessToken();
            }
        }
        return token;
    }

    private String acquireTokenCredentialFromCtx(ClientRequestContext requestContext) {
        MultivaluedMap<String, String> containerHeaders = (MultivaluedMap<String, String>) requestContext.getProperty(MpClientInvocation.CONTAINER_HEADERS);
        if (containerHeaders != null) {
            final List<String> authorizationHeader = containerHeaders.get("X-Authorization-acme_financial_auth");
            if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                return authorizationHeader.get(0).replace("Bearer ", "").trim();
            }
        }
        return "";
    }

}
