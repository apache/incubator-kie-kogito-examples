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
package org.kie.kogito.traffic;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import io.vertx.mutiny.core.Vertx;

@Service
public class TrafficViolationRestService {

    @Bean
    public Vertx vertx() {
        return Vertx.vertx();
    }

    private final URI uri;

    public TrafficViolationRestService() {
        uri = null;
    }

    @Autowired
    public TrafficViolationRestService(@Value("${traffic.violation.url}") String url) {
        uri = UriComponentsBuilder.fromUriString(url)
                .path("/Traffic Violation")
                .build()
                .toUri();
    }

    public TrafficViolationResponse evaluate(Driver driver, Violation violation) {
        return new RestTemplateBuilder()
                .build()
                .postForObject(uri, toMap(driver, violation), TrafficViolationResponse.class);
    }

    public Map<String, Object> toMap(Driver driver, Violation violation) {
        Map<String, Object> params = new HashMap<>();
        params.put("Violation", violation);
        params.put("Driver", driver);
        return params;
    }
}
