/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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

import com.google.common.io.Files;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.quarkus.cache.CacheResult;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kohsuke.github.GHAppInstallation;
import org.kohsuke.github.GHPermissionType;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

/**
 * Provides the installation token to interact with the GitHub API via GitHub App Installation
 *
 * @see <a href="https://github-api.kohsuke.org/githubappjwtauth.html">GitHub App Authentication via JWT token</a>
 */
@ApplicationScoped
public class TokenProvider {

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
        byte[] keyBytes = Files.toByteArray(new File(privateKeyPath));

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

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setIssuer(appId)
                .signWith(signingKey, signatureAlgorithm);

        long expMillis = nowMillis + expirationMillis;
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    @CacheResult(cacheName = "access_token", lockTimeout = cacheExpirationMillis)
    public String getToken() throws Exception {
        final GitHub gitHubApp = new GitHubBuilder().withJwtToken(createJWT()).build();
        final GHAppInstallation appInstall = gitHubApp.getApp().getInstallationById(installationId);

        return appInstall.createToken(getPermissions()).create().getToken();
    }

    private Map<String, GHPermissionType> getPermissions() {
        final Map<String, GHPermissionType> permissions = new HashMap<>();
        permissions.put("pull_requests", GHPermissionType.WRITE);
        permissions.put("issues", GHPermissionType.WRITE);
        return permissions;
    }
}
