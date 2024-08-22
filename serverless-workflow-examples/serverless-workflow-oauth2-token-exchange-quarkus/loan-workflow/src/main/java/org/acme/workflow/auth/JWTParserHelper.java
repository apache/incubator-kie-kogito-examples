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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;

import java.util.Base64;

/**
 * Used in the workflow to parse the JWT Token and retrieve user's info from the custom header.
 *
 * @see <a href="https://quarkus.io/guides/security-jwt#jwt-parser">Parse and Verify JsonWebToken with JWTParser</a>
 * @see <a href="https://metamug.com/article/security/decode-jwt-java.html">Decode JWT Token and Verify in Plain Java</a>
 */
@ApplicationScoped
public class JWTParserHelper {

    @Inject
    JWTParser jwtParser;

    ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode extractUser(String token) throws InvalidJwtException, JsonProcessingException {
        final String[] tokenParts = token.split("\\.");
        if (tokenParts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        return objectMapper.readTree(
                JwtClaims.parse(
                        new String(Base64.getUrlDecoder().decode(tokenParts[1]))).getRawJson());
    }

}
