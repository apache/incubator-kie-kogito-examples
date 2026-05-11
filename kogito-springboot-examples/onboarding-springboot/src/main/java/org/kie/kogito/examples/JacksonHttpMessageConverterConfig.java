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
package org.kie.kogito.examples;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;

// TODO Jackson 3 migration: Spring Boot 4 stops auto-registering MappingJackson2HttpMessageConverter
// even when a Jackson 2 ObjectMapper bean is present (the auto-configured HTTP converter is now
// MappingJacksonHttpMessageConverter on Jackson 3, which writes timestamps in UTC `Z` regardless
// of the timezone configured on the kogito-codegen-generated GlobalObjectMapper). Without the
// Jackson 2 converter Spring MVC produces the wrong timezone format and the OnboardingEndpointIT
// payroll.paymentDate assertion fails.
//
// This module has no rule units, so the codegen-generated RestObjectMapper (which would normally
// register this converter) is not produced — we register it locally instead. The canWrite override
// on String mirrors the codegen-template logic so DMN endpoints' pre-serialized String responses
// continue to flow through StringHttpMessageConverter unchanged.
//
// Drop in lock-step with the broader Jackson 3 migration.
@Configuration
public class JacksonHttpMessageConverterConfig {

    @Bean
    @ConditionalOnMissingBean(MappingJackson2HttpMessageConverter.class)
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper) {
            @Override
            public boolean canWrite(Class<?> clazz, MediaType mediaType) {
                if (clazz == String.class) {
                    return false;
                }
                return super.canWrite(clazz, mediaType);
            }
        };
    }
}
