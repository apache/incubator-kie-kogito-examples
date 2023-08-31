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
package org.kogito.examples.sw.github.service;

import java.io.File;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHPermissionType;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.quarkus.cache.CacheResult;

/**
 * Provides the installation token to interact with the GitHub API via GitHub App Installation
 *
 * @see <a href="https://github-api.kohsuke.org/githubappjwtauth.html">GitHub App Authentication via JWT token</a>
 */
@ApplicationScoped
public class TokenProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    private static final int expirationMillis = 600000;
    private static final int cacheExpirationMillis = 300000;

    @ConfigProperty(name = "org.kogito.examples.sw.github.service.app_id")
    String appId;

    @ConfigProperty(name = "org.kogito.examples.sw.github.service.key")
    String privateKeyPath;

    @ConfigProperty(name = "org.kogito.examples.sw.github.service.installation_id")
    Long installationId;

    public TokenProvider() {

    }

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = FileUtils.readFileToByteArray(new File(privateKeyPath));

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public String createJWT() throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our private key
        Key signingKey = getPrivateKey();

        // creating builder and serializer directly instead of relying on reflection for native use cases
        //Let's set the JWT Claims
        JwtBuilder builder = new DefaultJwtBuilder()
                .setIssuedAt(now)
                .setIssuer(appId)
                .signWith(signingKey, signatureAlgorithm);
        builder.serializeToJsonWith(new JacksonSerializer<>());

        long expMillis = nowMillis + expirationMillis;
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);

        //Builds the JWT and serializes it to a compact, URL-safe string
        final String token = builder.compact();
        LOGGER.info("JWT token generated and signed successfully");
        return token;
    }

    @CacheResult(cacheName = "access_token", lockTimeout = cacheExpirationMillis)
    public String getToken() throws Exception {
        final GitHub gitHubApp = new GitHubBuilder().withJwtToken(createJWT()).build();
        final GHAppInstallation appInstall = gitHubApp.getApp().getInstallationById(installationId);
        LOGGER.info("Attempt to generate token to GH Account {}", appInstall.getAccount().getName());
        final String token = appInstall.createToken(getPermissions()).create().getToken();
        LOGGER.info("Final token generated successfully");
        return token;
    }

    private Map<String, GHPermissionType> getPermissions() {
        final Map<String, GHPermissionType> permissions = new HashMap<>();
        permissions.put("pull_requests", GHPermissionType.WRITE);
        permissions.put("issues", GHPermissionType.WRITE);
        return permissions;
    }
}
