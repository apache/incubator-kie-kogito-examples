/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;

/**
 * Defines OpenAPI configurations for the Quarkus application, for more information you must see
 * <a href="https://quarkus.io/guides/openapi-swaggerui>Using OpenAPI and Swagger UI</a>
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Acme Financial Service API",
                version = "1.0.1"),
        components = @Components(
                securitySchemes = {
                        @SecurityScheme(securitySchemeName = "acme-financial-oauth",
                                type = SecuritySchemeType.OAUTH2,
                                flows = @OAuthFlows(
                                        clientCredentials = @OAuthFlow(
                                                authorizationUrl = "http://localhost:8281/auth/realms/kogito/protocol/openid-connect/auth",
                                                tokenUrl = "http://localhost:8281/auth/realms/kogito/protocol/openid-connect/token",
                                                scopes = {})))
                }))
public class AcmeFinancialApplication extends jakarta.ws.rs.core.Application {

}
