/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.acme.traffic;

import java.net.URI;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LicenseValidationRestService {

    private final URI uri;

    public LicenseValidationRestService() {
        uri = null;
    }

    @Autowired
    public LicenseValidationRestService(@Value("${license.validation.url}") String url) {
        uri = UriComponentsBuilder.fromUriString(url)
                .path("/validation/first")
                .build()
                .toUri();
    }

    public Driver evaluate(Driver driver) {
        return new RestTemplateBuilder()
                .build()
                .postForObject(uri, Collections.singletonMap("driver", driver), Driver.class);
    }
}